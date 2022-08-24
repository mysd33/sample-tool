package com.example.tool;

import java.text.MessageFormat;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class Tool {
	private final ThreadPoolTaskExecutor taskExecutor;
	private final RestTemplate restTemplate;

	@Value("${tool.multiplicity}")
	private int multiplicity;
	
	@Value("${tool.loop}")
	private int loop;

	@Value("${api.backend.url}/api/v1/todos")
	private String urlTodos;

	public void execuite() {
		taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
		for (int i = 0; i < multiplicity; i++) {
			taskExecutor.execute(() -> {
				for (int j = 0; j < loop; j++) {
					log.debug(MessageFormat.format("api call loop: {0}", j));
					Collection<Todo> todoList = findAll();
					todoList.forEach(todo -> {
						log.debug(MessageFormat.format("api call result: [{0}]", todo.toString()));
					});				
				}
			});						
		}
		taskExecutor.shutdown();
	}

	private Collection<Todo> findAll() {
		TodoList todoList = restTemplate.getForObject(urlTodos, TodoList.class);
		return todoList;
	}
}
