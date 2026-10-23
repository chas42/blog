package com.chas.blog.service;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;

import com.chas.blog.repository.PostRepository;

public class PostServiceTest {

    @Mock PostRepository repository;
	Clock clock = Clock.fixed(Instant.parse("2026-10-22T12:00:00Z"), ZoneOffset.UTC);
	PostService service;

	@BeforeEach
	void setUp() {
		service = new PostServiceImpl(repository, clock);
	}

}
