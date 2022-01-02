package telran.b7a.bookentitymanager.book.dao;

import java.util.Optional;

import telran.b7a.bookentitymanager.book.model.Author;

public interface AuthorRepository {
	Optional<Author> findById(String id);
	
	Author save(Author author);

	void delete(Author author);
}
