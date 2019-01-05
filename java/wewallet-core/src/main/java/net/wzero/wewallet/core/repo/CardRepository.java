package net.wzero.wewallet.core.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import net.wzero.wewallet.core.domain.Card;

public interface CardRepository extends JpaRepository<Card, Integer> {
	List<Card> findByMemberId(Integer memberId);
	/**
	 * 可以通过 memberId和addr 做个索引，做为唯一
	 * @param memberId
	 * @param addr
	 * @return
	 */
	Card findByMemberIdAndAddr(Integer memberId,String addr);
}
