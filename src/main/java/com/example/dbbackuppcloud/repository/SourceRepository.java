package com.example.dbbackuppcloud.repository;

import com.example.dbbackuppcloud.entity.Source;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface SourceRepository extends CrudRepository<Source, Long> {
    List<Source> findAll();
}
