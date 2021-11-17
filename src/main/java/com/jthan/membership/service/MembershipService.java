package com.jthan.membership.service;

import com.jthan.membership.dto.MembershipResponse;
import com.jthan.membership.entity.Membership;
import com.jthan.membership.enums.MembershipType;
import com.jthan.membership.exception.MembershipErrorResult;
import com.jthan.membership.exception.MembershipException;
import com.jthan.membership.repository.MembershipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MembershipService {

	private final MembershipRepository membershipRepository;

	public MembershipResponse addMembership(final String userId, final MembershipType membershipType, final Integer point) {
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

		return MembershipResponse.builder()
			.id(savedMembership.getId())
			.membershipType(savedMembership.getMembershipType())
			.build();
	}
}
