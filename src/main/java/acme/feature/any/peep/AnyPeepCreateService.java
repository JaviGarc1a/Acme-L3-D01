/*
 * AnyShoutCreateService.java
 *
 * Copyright (C) 2012-2023 Rafael Corchuelo.
 *
 * In keeping with the traditional purpose of furthering education and research, it is
 * the policy of the copyright owner to permit non-commercial use and redistribution of
 * this software. It has been tested carefully, but it is not guaranteed for any particular
 * purposes. The copyright owner does not offer any warranties or representations, nor do
 * they accept any liabilities with respect to them.
 */

package acme.feature.any.peep;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.entities.peep.Peep;
import acme.framework.components.accounts.Any;
import acme.framework.components.accounts.UserAccount;
import acme.framework.components.models.Tuple;
import acme.framework.helpers.MomentHelper;
import acme.framework.services.AbstractService;

@Service
public class AnyPeepCreateService extends AbstractService<Any, Peep> {

	// Internal state ---------------------------------------------------------

	@Autowired
	protected AnyPeepRepository repository;

	// AbstractService interface ----------------------------------------------


	@Override
	public void check() {
		super.getResponse().setChecked(true);
	}

	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Peep object;
		UserAccount user;
		user = this.repository.findUserByUsername(super.getRequest().getPrincipal().getUsername());
		final String fullname = user.getIdentity().getName() + " " + user.getIdentity().getSurname();

		object = new Peep();
		if (super.getRequest().getPrincipal().isAnonymous())
			object.setNick("");
		else
			object.setNick(fullname);

		object.setMoment(MomentHelper.getCurrentMoment());
		super.getBuffer().setData(object);
	}

	@Override
	public void bind(final Peep object) {
		assert object != null;

		final Tuple tuple;

		super.bind(object, "title", "moment", "nick", "message", "email", "link");
	}

	@Override
	public void validate(final Peep object) {
		assert object != null;

		if (!super.getBuffer().getErrors().hasErrors("moment")) {
			boolean momentError;

			momentError = MomentHelper.isPast(object.getMoment());

			super.state(momentError, "moment", "assistant.peep.form.error.moment-not-past");
		}
	}

	@Override
	public void perform(final Peep object) {
		assert object != null;

		this.repository.save(object);
	}

	@Override
	public void unbind(final Peep object) {
		assert object != null;

		Tuple tuple;

		tuple = super.unbind(object, "title", "moment", "nick", "message", "email", "link");

		super.getResponse().setData(tuple);

	}

}
