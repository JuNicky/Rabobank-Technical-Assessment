package com.nicky.rabobank.technical.assessment.repository;

import com.nicky.rabobank.technical.assessment.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Integer> {
}
