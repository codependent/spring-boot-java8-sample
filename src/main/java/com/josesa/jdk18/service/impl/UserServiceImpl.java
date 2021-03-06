package com.josesa.jdk18.service.impl;

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.josesa.jdk18.entity.Person;
import com.josesa.jdk18.repository.PersonRepository;
import com.josesa.jdk18.service.UserService;

@Service("userService")
@Profile("not-cached")
@Transactional
public class UserServiceImpl implements UserService{

	@Autowired
	private PersonRepository personDAO;
	
	@Override
	public Person save(Person p) {
		return personDAO.save(p);
	}
	
	@Override
	public Person get(long id) {
		Optional<Person> searched = personDAO.findOne(id);
		return searched.isPresent() ? searched.get() : null;
	}
	
	@Override
	public List<Person> search(Predicate<Person> criteria){
		return search(criteria, 0);
	}
	
	@Override
	public List<Person> search(Predicate<Person> criteria, int maxResults){
		if(maxResults >0){
			try (Stream<Person> stream = personDAO.streamAllPeople()) {
				return stream.parallel()
				.filter(criteria).limit(maxResults)
				.collect(Collectors.toList());
			}
		}else{
			try (Stream<Person> stream = personDAO.streamAllPeople()) {
				return stream.parallel()
						.filter(criteria)
						.collect(Collectors.toList());
			}
		}
	}
	
	@Override
	public void print(List<Person> people) {
		logger.info("Printing {} people:", people.size());
		people.stream().parallel().forEach( p -> logger.info("{}", p) );
	}
	
	@Override
	public Stream<Person> printAndReturnStream(List<Person> people) {
		return people.stream().parallel().peek( p ->  logger.info("{}", p) );
	}

	@Override
	public Iterator<Entry<Long, Person>> iterator() {
		throw new IllegalAccessError("Implementación UserServiceImpl no tiene caché y no puede iterarse");
	}

	

}
