package com.jthan.membership.repository;

import com.jthan.membership.entity.Membership;
import com.jthan.membership.enums.MembershipType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MembershipRepository extends JpaRepository<Membership,Long> {

	Membership findByUserIdAndMembershipType(final String userId, final MembershipType membershipType);
}
