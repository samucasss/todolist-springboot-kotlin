package com.samuca.todolist.dao

import com.samuca.todolist.model.Tecnologia
import org.springframework.data.mongodb.repository.MongoRepository

interface TecnologiaDao: MongoRepository<Tecnologia, String>