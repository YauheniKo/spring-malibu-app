package com.malibu.springmalibuapp.repository;

import com.malibu.springmalibuapp.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    Tag findTagByName(String name);
}
