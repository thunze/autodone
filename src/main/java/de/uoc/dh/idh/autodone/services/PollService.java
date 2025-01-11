package de.uoc.dh.idh.autodone.services;

import static de.uoc.dh.idh.autodone.config.MastodonConfig.MASTODON_API_MEDIA;
import static de.uoc.dh.idh.autodone.utils.ObjectUtils.FORCE;
import static de.uoc.dh.idh.autodone.utils.ObjectUtils.copyFields;
import static de.uoc.dh.idh.autodone.utils.WebUtils.remoteHref;
import static de.uoc.dh.idh.autodone.utils.WebUtils.request;
import static org.springframework.data.domain.Sort.by;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;

import de.uoc.dh.idh.autodone.base.BaseService;
import de.uoc.dh.idh.autodone.entities.PollEntity;
import de.uoc.dh.idh.autodone.repositories.PollRepository;
import jakarta.transaction.Transactional;

@Service()
@Transactional()
public class PollService extends BaseService<PollEntity> {

	@Autowired()
	private PollRepository pollRepository;

	//

	public PollEntity publish(UUID uuid) {
		return publish(pollRepository.findById(uuid).get());
	}

	public PollEntity publish(PollEntity poll) {
		var data = new LinkedMultiValueMap<String, Object>();
		data.add("description", poll.description);
		data.add("name", poll.uuid.toString());

		var href = remoteHref(poll.status.group.token.server.domain, MASTODON_API_MEDIA);
		var post = request(PollEntity.class).auth(poll.status.group.token).form().post(href, data);
		return save(copyFields(post, poll, FORCE));
	}

	//

	public PollEntity save(PollEntity poll) {
		return pollRepository.save(poll);
	}

	//

	@Override()
	public void delete(UUID uuid, String username, String domain) {
		pollRepository //
				.deleteByUuidAndStatusGroupTokenUsernameAndStatusGroupTokenServerDomain(uuid, username, domain);
	}

	@Override()
	public PollEntity getOne(UUID uuid, String username, String domain) {
		return pollRepository //
				.findOneByUuidAndStatusGroupTokenUsernameAndStatusGroupTokenServerDomain(uuid, username, domain);
	}

	@Override
	public Iterable<PollEntity> getOwn(String username, String domain) {
		return pollRepository //
				.findAllByStatusGroupTokenUsernameAndStatusGroupTokenServerDomain(username, domain);
	}

	@Override()
	public Page<PollEntity> getPage(Pageable request, String username, String domain) {
		return pollRepository //
				.findAllByStatusGroupTokenUsernameAndStatusGroupTokenServerDomain(request, username, domain);
	}

	//

	@Override()
	protected Sort getSort() {
		return by("status.group.name");
	}

}
