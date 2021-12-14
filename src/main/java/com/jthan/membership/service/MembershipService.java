package com.jthan.membership.service;

import com.jthan.membership.dto.MembershipAddResponse;
import com.jthan.membership.dto.MembershipDetailResponse;
import com.jthan.membership.entity.Membership;
import com.jthan.membership.enums.MembershipType;
import com.jthan.membership.exception.MembershipErrorResult;
import com.jthan.membership.exception.MembershipException;
import com.jthan.membership.repository.MembershipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MembershipService {

	private final MembershipRepository membershipRepository;

	public MembershipAddResponse addMembership(final String userId, final MembershipType membershipType, final Integer point) {
		final Membership result = membershipRepository.findByUserIdAndMembershipType(userId,membershipType);
		if(result != null) {
			throw new MembershipException(MembershipErrorResult.DUPLICATED_MEMBERSHIP_REGISTER);
		}

		final Membership membership = Membership.builder()
			.userId(userId)
			.point(point)
			.membershipType(membershipType)
			.build();

		final Membership savedMembership = membershipRepository.save(membership);

		return MembershipAddResponse.builder()
			.id(savedMembership.getId())
			.membershipType(savedMembership.getMembershipType())
			.build();
	}

	public List<MembershipDetailResponse> getMembershipList(final String userId) {

		final List<Membership> membershipsList = membershipRepository.findAllByUserId(userId);

		return membershipsList.stream()
				.map(v -> MembershipDetailResponse.builder()
				.id(v.getId())
				.membershipType(v.getMembershipType())
				.point(v.getPoint())
				.createdAt(v.getCreatedAt())
				.build())
			.collect(Collectors.toList());
	}

	public MembershipDetailResponse getMembership(final String userId, final MembershipType membershipType) {
		final Membership findResult = membershipRepository.findByUserIdAndMembershipType(userId, membershipType);
		if(findResult == null) {
			throw new MembershipException(MembershipErrorResult.MEMBERSHIP_NOT_FOUND);
		}
		return MembershipDetailResponse.builder()
				.id(findResult.getId())
				.membershipType(findResult.getMembershipType())
				.point(findResult.getPoint())
				.createdAt(findResult.getCreatedAt())
				.build();
	}
}
