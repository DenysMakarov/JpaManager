package telran.b7a.bookentitymanager.book.dao;

import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import org.springframework.transaction.annotation.Transactional;
import telran.b7a.bookentitymanager.book.model.Author;

@Repository
public class AuthorRepositoryImpl implements AuthorRepository {
	
	@PersistenceContext
	EntityManager em;

	@Override
	@Transactional(readOnly = true)
	public Optional<Author> findById(String id) {
		return Optional.ofNullable(em.find(Author.class, id));
	}

	@Override
	@Transactional
	public Author save(Author author) {
		em.persist(author);
		return author;
	}

	@Override
	@Transactional
	public void delete(Author author) {
		em.remove(author);
	}

}
