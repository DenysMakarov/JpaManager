package telran.b7a.bookentitymanager.book.dao;

import java.util.Optional;
import java.util.stream.Stream;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import org.springframework.transaction.annotation.Transactional;
import telran.b7a.bookentitymanager.book.model.Book;
import telran.b7a.bookentitymanager.book.model.Publisher;

@Repository
public class BookRepositoryImpl implements BookRepository {
	
	@PersistenceContext
	EntityManager em;

	@Override
	@Transactional(readOnly = true)
	public Stream<Book> findByAuthorsName(String authorName) {
		TypedQuery<Book> query = em.createQuery("select b from Book b " + // jpql запрос
				"join b.authors a where a.name=?1", Book.class); // передаем параметры
		query.setParameter(1, authorName);
		return query.getResultStream();
	}

	@Override
	@Transactional(readOnly = true)
	public Stream<Book> findByPublisherPublisherName(String publisherName) {
		TypedQuery<Book> query = em.createQuery("select b from Book b " +
				"join b.publisher p where p.publisherName=?1", Book.class); // передаем параметры
		query.setParameter(1, publisherName);
		return query.getResultStream();
	}

	@Override
	@Transactional(readOnly = true)
	public boolean existsById(String id) {
		return em.find(Book.class, id) != null; // Book.class - в какой сущности ищем | id - какой первичный ключ
	}

	@Override
	@Transactional
	public Book save(Book book) {
		em.merge(book);
		return book;
	}

	@Override
//	@Transactional(readOnly = true)
	public Optional<Book> findById(String id) {
		return Optional.ofNullable(em.find(Book.class, id));
	}

	@Override
//	@Transactional
	public void delete(Book book) {
		em.remove(book);
	}

	@Override
	@Transactional
	public void delete(String isbn) {
		Book book = em.find(Book.class, isbn);
		em.remove(book);
	}

}
