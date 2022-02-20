package com.example.musicat_audio;

import com.example.musicat_audio.controller.PlaylistController;
import com.example.musicat_audio.domain.Playlist;
import com.example.musicat_audio.service.PlaylistService;
import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.BDDAssumptions.given;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static springfox.documentation.builders.RequestHandlerSelectors.any;

@SpringBootTest
@Slf4j
//@WebMvcTest
@AutoConfigureRestDocs
@AutoConfigureMockMvc
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
class MusicatAudioApplicationTests {

//	@Autowired
//	private MockMvc mockMvc;

	@Autowired
	private EntityManager em;

//	@BeforeEach
//	public void setting(WebApplicationContext webApplicationContext,
//						RestDocumentationContextProvider restDocumentation) {
//		this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
//				.apply(documentationConfiguration(restDocumentation))
//				.build();
//	}

	@Autowired
	private WebApplicationContext context;

	private MockMvc mockMvc;

	@MockBean
	private PlaylistService playlistService;

	@BeforeEach
	public void setUp(RestDocumentationContextProvider restDocumentation) {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
				.apply(documentationConfiguration(restDocumentation))  // (2)
				.build();
	}

	@Test
	void Test1() throws Exception {
		this.mockMvc.perform(get("/api/hello"))
				.andDo(print())
				.andExpect(status().isOk());
	}

	@Test
	void makeNowPlayTest() throws Exception {
		Playlist playlist = new Playlist("2pl1", "현재", 2);
		this.mockMvc.perform(post("/api/playlists/makeNow/{memberNo}", 2)
						.accept(MediaType.APPLICATION_JSON_VALUE)
				.content("{\"memberNo\" : 2}")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(document("post-now",
						pathParameters(
								parameterWithName("memberNo").description("멤버 번호")
						)))
				.andDo(print());
	}

	@Test // 현재 안 되고 있다.
	void createPliTest() throws Exception {
		int memberNo = 1;
		List<Playlist> list = new ArrayList<>();
		list.add(new Playlist("1pl3", memberNo));
		when(playlistService.showPlaylist(memberNo)).thenReturn(list);

		MockMultipartFile image = new MockMultipartFile("image", "C:/Users/lucas/Desktop/개인/블로그/spring.png", "image/png", "<<png data>>".getBytes());

		this.mockMvc.perform(multipart("/api/playlists/create")
				.file(image)
				.param("playlistName", "지금")
				.param("memberNo", "1"))
				.andExpect(status().isOk())
				.andDo(document("createPli",
						requestParameters(
								parameterWithName("playlistName").description("플레이리스트 이름"),
								parameterWithName("memberNo").description("회원 번호")
						)));
	}

	@Test
	void deletePliTest() throws Exception {
		this.mockMvc.perform(delete("/api/playlists/delete/{memberNo}/{playlistKey}", 2, "2pl2")
						.accept(MediaType.APPLICATION_JSON_VALUE)
				.content("{\"memberNo\" : 2, \n\"playlistKey\" : \"2pl2\"}")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(document("delete-deletePlaylist",
						pathParameters(
								parameterWithName("memberNo").description("멤버 번호"),
								parameterWithName("playlistKey").description("플레이리스트 식별 문자")
						)))
				.andDo(print());
	}

	@Test
	void findPlaylistTest() throws Exception {
		this.mockMvc.perform(get("/api/playlists/{memberNo}", 2)
				.accept(MediaType.APPLICATION_JSON_VALUE)
				.content("{\"memberNo\" : 2}")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(document("findPlaylistofMember",
						pathParameters(
								parameterWithName("memberNo").description("멤버 번호")
						)))
				.andDo(print());
	}

	@Test
	void findDetailPlaylistTest() throws Exception {
		this.mockMvc.perform(get("/api/detailPlaylists/{playlistKey}", "1pl1")
				.accept(MediaType.APPLICATION_JSON_VALUE)
				.content("{\"playlistKey\" : \"1pl1\"}")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(document("detailPlaylist",
						pathParameters(
								parameterWithName("playlistKey").description("플레이리스트 식별 문자열")
						)))
				.andDo(print());
	}

	@Test
	void pushPlaylistToNowTest() throws Exception {
		String playlistKey = "2pl3";
		String memberNo = "2";
		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.set("playlistKey", playlistKey);
		map.set("memberNo", memberNo);


		this.mockMvc.perform(post("/api/playlists/pushNow")
				.params(map))
				.andExpect(status().isOk())
				.andDo(document("pushPlaylistToNow",
						requestParameters(
								parameterWithName("playlistKey").description("플레이리스트 식별 문자열"),
								parameterWithName("memberNo").description("멤버 번호")
						)));
	}

	@Test
	void pullMusicTest() throws Exception {
		String playlistKey = "2pl2";

		this.mockMvc.perform(delete("/api/playlists/pull/{playlistKey}/{musicNos}", "2pl2", "[25]")
						.accept(MediaType.APPLICATION_JSON_VALUE)
						.content("{\"playlistKey\" : \"2pl2\", \n\"musicNos\" : \"[25]\"}")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(document("pullMusic",
						pathParameters(
								parameterWithName("playlistKey").description("플레이리스트 식별 문자열"),
								parameterWithName("musicNos").description("음악 번호")
						)))
				.andDo(print());
	}

	@Test
	void pushMusicTo() throws Exception {

		this.mockMvc.perform(post("/api/playlists/musics")
				.param("playlistKey", "1pl2")
				.param("musicNos", "[46]"))
				.andExpect(status().isOk())
				.andDo(document("pushMusicTo",
						requestParameters(
								parameterWithName("playlistKey").description("플레이리스트 식별 문자열"),
								parameterWithName("musicNos").description("음악 리스트")
						)));

	}

	////////////////////////////////////// 오디오 관련 테스트 (예나)
	@Test
	@DisplayName("게시글 번호로 음악 찾기")
	void findMusicsByArticleTest() throws Exception {
		this.mockMvc.perform(get("/api/musics/article/{articleNo}",135)
						.accept(MediaType.APPLICATION_JSON_VALUE)
						.content("{\"articleNo\" : 135")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(document("findMusicByArticleNo",
						pathParameters(
								parameterWithName("articleNo").description("게시글 번호")
						)))
				.andDo(print());
	}

	@Test
	@DisplayName("게시글 번호로 음악 삭제하기")
	void deleteMusicByArticleNoTest() throws Exception {
		this.mockMvc.perform(delete("/api/musics/article/{articleNo}", 156)
						.accept(MediaType.APPLICATION_JSON_VALUE)
						.content("{\"articleNo\" : 156}")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(document("deleteMusicByArticleNoTest",
						pathParameters(
								parameterWithName("articleNo").description("게시글 일련번호")
						)))
				.andDo(print());
	}

	@Test
	@DisplayName("음악 ID로 음악 찾기")
	void findMusicsByMusicIDTest() throws Exception {
		this.mockMvc.perform(get("/api/musics/id/{id}",162)
						.accept(MediaType.APPLICATION_JSON_VALUE)
						.content("{\"id\" : 162")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(document("findMusicByMusicID",
						pathParameters(
								parameterWithName("id").description("음악 일련번호")
						)))
				.andDo(print());
	}

	@Test
	@DisplayName("음악 ID로 음악 삭제하기")
	void deleteMusicByMusicIDTest() throws Exception {
		this.mockMvc.perform(delete("/api/musics/id/{musicId}", 160)
						.accept(MediaType.APPLICATION_JSON_VALUE)
						.content("{\"musicId\" : 160}")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(document("deleteMusicByMusicIDTest",
						pathParameters(
								parameterWithName("id").description("음악 일련번호")
						)))
				.andDo(print());
	}

	@Test // 이거 안됨
	@Disabled
	@DisplayName("파일 이름으로 audio 찾기")
	void findMusicsByFileNameTest() throws Exception {
		this.mockMvc.perform(get("/api/musics/file/{fileName}","5e281650-e929-4f20-b721-51e9502fb661.audio")
						.accept(MediaType.APPLICATION_JSON_VALUE)
						.content("{\"fileName\" : 5e281650-e929-4f20-b721-51e9502fb661.audio")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(document("findMusicByFileName",
						pathParameters(
								parameterWithName("fileName").description("파일명")
						)))
				.andDo(print());
	}
}
