package com.jthan.membership.controller;

import com.google.gson.Gson;
import com.jthan.membership.dto.MembershipDetailResponse;
import com.jthan.membership.dto.MembershipRequest;
import com.jthan.membership.dto.MembershipAddResponse;
import com.jthan.membership.enums.MembershipType;
import com.jthan.membership.exception.MembershipErrorResult;
import com.jthan.membership.exception.MembershipException;
import com.jthan.membership.service.MembershipService;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static com.jthan.membership.constants.MembershipConstants.USER_ID_HEADER;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class MembershipControllerTest {

	@InjectMocks
	private MembershipController target;

	@Mock
	private MembershipService membershipService;

	private MockMvc mockMvc;
	private Gson gson;

	@BeforeEach
	public void init() {
		gson = new Gson();
		mockMvc = MockMvcBuilders.standaloneSetup(target)
			.build();
	}

	@Test
	public void 멤버십등록실패_사용자식별값이헤더에없음() throws Exception {
		// given
		final String url = "/api/v1/membership";

		// when
		final ResultActions resultActions = mockMvc.perform(
			MockMvcRequestBuilders.post(url)
				.content(gson.toJson(membershipRequest(10000, MembershipType.NAVER)))
				.contentType(MediaType.APPLICATION_JSON)
		);

		// then
		resultActions.andExpect(status().isBadRequest());
	}

	private MembershipRequest membershipRequest(final Integer point, final MembershipType membershipType) {
		return MembershipRequest.builder()
			.point(point)
			.membershipType(membershipType)
			.build();
	}

	@Test
	public void 멤버십등록실패_포인트가null() throws Exception {
		// given
		final String url = "/api/v1/membership";

		// when
		final ResultActions resultActions = mockMvc.perform(
			MockMvcRequestBuilders.post(url)
				.header(USER_ID_HEADER, "12345")
				.content(gson.toJson(membershipRequest(null, MembershipType.NAVER)))
				.contentType(MediaType.APPLICATION_JSON)
		);

		// then
		resultActions.andExpect(status().isBadRequest());
	}

	@Test
	public void 멤버십등록실패_포인트가음수() throws Exception {
		// given
		final String url = "/api/v1/membership";

		// when
		final ResultActions resultActions = mockMvc.perform(
			MockMvcRequestBuilders.post(url)
				.header(USER_ID_HEADER, "12345")
				.content(gson.toJson(membershipRequest(-1, MembershipType.NAVER)))
				.contentType(MediaType.APPLICATION_JSON)
		);

		// then
		resultActions.andExpect(status().isBadRequest());
	}

	@Test
	public void 멤버십등록실패_멤버십종류가Null() throws Exception {
		// given
		final String url = "/api/v1/membership";

		// when
		final ResultActions resultActions = mockMvc.perform(
			MockMvcRequestBuilders.post(url)
				.header(USER_ID_HEADER, "12345")
				.content(gson.toJson(membershipRequest(10000,null)))
				.contentType(MediaType.APPLICATION_JSON)
		);

		// then
		resultActions.andExpect(status().isBadRequest());
	}

	@Test
	public void 멤버십등록실패_MemberService에서에러Throw() throws Exception {
		// given
		final String url = "/api/v1/membership";
		doThrow(new MembershipException(MembershipErrorResult.DUPLICATED_MEMBERSHIP_REGISTER))
			.when(membershipService)
			.addMembership("12345", MembershipType.NAVER, 10000);

		// when
		final ResultActions resultActions = mockMvc.perform(
			MockMvcRequestBuilders.post(url)
				.header(USER_ID_HEADER, "12345")
				.content(gson.toJson(membershipRequest(10000,MembershipType.NAVER)))
				.contentType(MediaType.APPLICATION_JSON)
		);

		// then
		resultActions.andExpect(status().isBadRequest());
	}

	@Test
	public void 멤버십등록성공() throws Exception {
		// given
		final String url = "/api/v1/membership";
		final MembershipAddResponse membershipAddResponse = MembershipAddResponse.builder()
			.id(-1L)
			.membershipType(MembershipType.NAVER).build();

		doReturn(membershipAddResponse).when(membershipService).addMembership("12345", MembershipType.NAVER, 10000);

		// when
		final ResultActions resultActions = mockMvc.perform(
			MockMvcRequestBuilders.post(url)
				.header(USER_ID_HEADER, "12345")
				.content(gson.toJson(membershipRequest(10000,MembershipType.NAVER)))
				.contentType(MediaType.APPLICATION_JSON)
		);

		// then
		resultActions.andExpect(status().isCreated());

		final MembershipAddResponse response = gson.fromJson(resultActions.andReturn()
			.getResponse()
			.getContentAsString(StandardCharsets.UTF_8), MembershipAddResponse.class);

		assertThat(response.getMembershipType()).isEqualTo(MembershipType.NAVER);
		assertThat(response.getId()).isNotNull();
	}

	@Test
	public void 멤버십목록조회실패_사용자식별값이헤더에없음() throws Exception {
		// given
		final String url = "/api/v1/membership/list";

		// when
		final ResultActions resultActions = mockMvc.perform(
				MockMvcRequestBuilders.get(url)
		);

		// then
		resultActions.andExpect(status().isBadRequest());
	}

	@Test
	public void 멤버십목록조회성공() throws Exception {
		// given
		final String url = "/api/v1/membership/list";

		doReturn(Arrays.asList(
				MembershipDetailResponse.builder().build(),
				MembershipDetailResponse.builder().build(),
				MembershipDetailResponse.builder().build()
		)).when(membershipService).getMembershipList("12345");

		// when
		final ResultActions resultActions = mockMvc.perform(
				MockMvcRequestBuilders.get(url)
				.header(USER_ID_HEADER, "12345")
		);

		// then
		resultActions.andExpect(status().isOk());
	}

	@Test
	public void 멤버십상세조회실패_사용자식별값이헤더에없음() throws Exception {
		// given
		final String url = "/api/v1/membership";

		// when
		final ResultActions resultActions = mockMvc.perform(
				MockMvcRequestBuilders.get(url)
		);

		// then
		resultActions.andExpect(status().isBadRequest());
	}

	@Test
	public void 멤버십상세조회실패_멤버십타입이파라미터에없음() throws Exception {
		// given
		final String url = "/api/v1/membership";

		// when
		final ResultActions resultActions = mockMvc.perform(
				MockMvcRequestBuilders.get(url)
				.header(USER_ID_HEADER, "12345")
				.param("membershipType", "empty")
		);

		// then
		resultActions.andExpect(status().isBadRequest());
	}

	@Test
	public void 멤버십상세조회실패_멤버십이존재하지않음() throws Exception {
		// given
		final String url = "/api/v1/membership";
		doThrow(new MembershipException(MembershipErrorResult.MEMBERSHIP_NOT_FOUND))
				.when(membershipService)
				.getMembership("12345", MembershipType.NAVER);

		// when
		final ResultActions resultActions = mockMvc.perform(
				MockMvcRequestBuilders.get(url)
				.header(USER_ID_HEADER, "12345")
				.param("membershipType", MembershipType.NAVER.name())
		);
		// then
		resultActions.andExpect(status().isNotFound());
	}

	@Test
	public void 멤버십상세조회성공() throws Exception {
		// given
		final String url = "/api/v1/membership";
		doReturn(
				MembershipDetailResponse.builder().build()
		).when(membershipService).getMembership("12345", MembershipType.NAVER);

		// when
		final ResultActions resultActions = mockMvc.perform(
				MockMvcRequestBuilders.get(url)
				.header(USER_ID_HEADER, "12345")
				.param("membershipType", MembershipType.NAVER.name())
		);
		// then
		resultActions.andExpect(status().isOk());
	}
}
















