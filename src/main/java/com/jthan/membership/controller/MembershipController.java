package com.jthan.membership.controller;

import com.jthan.membership.common.DefaultRestController;
import com.jthan.membership.dto.MembershipDetailResponse;
import com.jthan.membership.dto.MembershipRequest;
import com.jthan.membership.dto.MembershipAddResponse;
import com.jthan.membership.enums.MembershipType;
import com.jthan.membership.service.MembershipService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.List;

import static com.jthan.membership.constants.MembershipConstants.USER_ID_HEADER;

@RestController
@RequiredArgsConstructor
public class MembershipController extends DefaultRestController {

	private final MembershipService membershipService;

	@PostMapping("/api/v1/membership")
	public ResponseEntity<MembershipAddResponse> addMembership(
		@RequestHeader(USER_ID_HEADER) final String userId,
		@RequestBody @Valid final MembershipRequest membershipRequest) {

		final MembershipAddResponse membershipAddResponse =  membershipService.addMembership(userId, membershipRequest.getMembershipType(), membershipRequest.getPoint());

		return ResponseEntity.status(HttpStatus.CREATED).body(membershipAddResponse);
	}

	@GetMapping("/api/v1/membership/list")
	public ResponseEntity<List<MembershipDetailResponse>> getMembershipList(
			@RequestHeader(USER_ID_HEADER) final String userId) {
		return ResponseEntity.ok(membershipService.getMembershipList(userId));
	}

	@GetMapping("/api/v1/membership")
	public ResponseEntity<MembershipDetailResponse> getMembership(
			@RequestHeader(USER_ID_HEADER) final String userId,
			@RequestParam final MembershipType membershipType) {

		return ResponseEntity.ok(membershipService.getMembership(userId,membershipType));
	}

	@DeleteMapping("/api/v1/membership/{id}")
	public ResponseEntity<Void> getMembership(
			@RequestHeader(USER_ID_HEADER) final String userId,
			@PathVariable final Long id) {

		membershipService.removeMembership(id, userId);
		return ResponseEntity.noContent().build();
	}
}
