package com.jthan.membership.repository;

import com.jthan.membership.entity.Membership;
import com.jthan.membership.enums.MembershipType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MembershipRepository extends JpaRepository<Membership,Long> {

	Membership findByUserIdAndMembershipType(final String userId, final MembershipType membershipType);

	List<Membership> findAllByUserId(final String userId);
}
