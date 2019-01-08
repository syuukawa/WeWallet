package net.wzero.wewallet.core.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import net.wzero.wewallet.domain.EntityBase;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name="cards")
public class Card extends EntityBase {
	@Id
	@GeneratedValue
	private Integer id;
	// 账户的Id,这里是不同模块，所以不能强关联
	private Integer memberId;
	@JoinColumn(name="card_type_id")
	@ManyToOne
	private CardType cardType;
	//bip39 的路径
	private String path;
	//账户地址,方便其他业务，反正地址不可能推算出私钥
	private String addr;
	@Column(columnDefinition="tinyint(1) default 0")
	private Boolean isRefreshing;
	@Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP", updatable = false)
	private Date created;
	@Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP", updatable = false)
	private Date updated;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getMemberId() {
		return memberId;
	}
	public void setMemberId(Integer memberId) {
		this.memberId = memberId;
	}
	public CardType getCardType() {
		return cardType;
	}
	public void setCardType(CardType cardType) {
		this.cardType = cardType;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getAddr() {
		return addr;
	}
	public void setAddr(String addr) {
		this.addr = addr;
	}
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	public Date getUpdated() {
		return updated;
	}
	public void setUpdated(Date updated) {
		this.updated = updated;
	}
	public Boolean getIsRefreshing() {
		return isRefreshing;
	}
	public void setIsRefreshing(Boolean isRefreshing) {
		this.isRefreshing = isRefreshing;
	}
	
}
