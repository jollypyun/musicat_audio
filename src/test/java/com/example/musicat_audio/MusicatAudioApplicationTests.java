package com.example.musicat_audio;

import com.example.musicat_audio.controller.PlaylistController;
import com.example.musicat_audio.domain.Playlist;
import com.example.musicat_audio.service.PlaylistService;
import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;

import java.io.File;
import static org.assertj.core.api.BDDAssumptions.given;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
	void nowTest() throws Exception {
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

	@Test
	void deletePlaylistTest() throws Exception {
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
}
