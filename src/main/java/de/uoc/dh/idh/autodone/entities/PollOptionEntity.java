package de.uoc.dh.idh.autodone.entities;

import static jakarta.persistence.GenerationType.UUID;

import java.util.UUID;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data()
@Entity()
@JsonNaming(SnakeCaseStrategy.class)
public class PollOptionEntity {

	@Id()
	@GeneratedValue(strategy = UUID)
	public UUID uuid;

	//

	@ManyToOne(optional = false)
	public PollEntity poll;

	//

	@Column(length = 50)
	public String option;

	@Column()
	public String id;
}
