package telran.b7a.bookentitymanager.book.service;

import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import telran.b7a.bookentitymanager.book.dao.AuthorRepository;
import telran.b7a.bookentitymanager.book.dao.BookRepository;
import telran.b7a.bookentitymanager.book.dao.PublisherRepository;
import telran.b7a.bookentitymanager.book.dto.AuthorDto;
import telran.b7a.bookentitymanager.book.dto.BookDto;
import telran.b7a.bookentitymanager.book.dto.exceptions.EntityNotFoundException;
import telran.b7a.bookentitymanager.book.model.Author;
import telran.b7a.bookentitymanager.book.model.Book;
import telran.b7a.bookentitymanager.book.model.Publisher;

@Service
public class BookServiceImpl implements BookService {
	
	ModelMapper modelMapper;
	BookRepository bookRepository;
	AuthorRepository authorRepository;
	PublisherRepository publisherRepository;
	
	@Autowired
	public BookServiceImpl(ModelMapper modelMapper, BookRepository bookRepository, AuthorRepository authorRepository,
			PublisherRepository publisherRepository) {
		this.modelMapper = modelMapper;
		this.bookRepository = bookRepository;
		this.authorRepository = authorRepository;
		this.publisherRepository = publisherRepository;
	}

	@Override
	@Transactional
	public boolean addBook(BookDto bookDto) {
		if (bookRepository.existsById(bookDto.getIsbn())) {
			return false;
		}
		//Publisher
		Publisher publisher = publisherRepository.findById(bookDto.getPublisher())
					.orElseGet(() -> publisherRepository.save(new Publisher(bookDto.getPublisher())));
		//Author
		Set<Author> authors = bookDto.getAuthors().stream()
				.map(a -> authorRepository.findById(a.getName()).orElseGet(() -> authorRepository.save(new Author(a.getName(), a.getBirthDate()))))
				.collect(Collectors.toSet());
		Book book = new Book(bookDto.getIsbn(), bookDto.getTitle(), authors, publisher);
		bookRepository.save(book);
		return true;
	}

	@Override
	@Transactional(readOnly = true)
	public BookDto findBookByIsbn(String isbn) {
		Book book = bookRepository.findById(isbn).orElseThrow(() -> new EntityNotFoundException());
		return modelMapper.map(book, BookDto.class);
	}

	@Override
	@Transactional
	public BookDto removeBook(String isbn) {
		Book book = bookRepository.findById(isbn).orElseThrow(() -> new EntityNotFoundException());
		bookRepository.delete(book);
		return modelMapper.map(book, BookDto.class);
	}

	@Override
	@Transactional
	public BookDto updateBook(String isbn, String title) {
		Book book = bookRepository.findById(isbn).orElseThrow(() -> new EntityNotFoundException());
		book.setTitle(title);
		return modelMapper.map(book, BookDto.class);
	}

	@Override
	@Transactional(readOnly = true)
	public Iterable<BookDto> findBooksByAuthor(String authorName) {
//		return bookRepository.findByAuthorsName(authorName)
//				.map(b -> modelMapper.map(b, BookDto.class))
//				.collect(Collectors.toList());
		Author author = authorRepository.findById(authorName).orElseThrow(() -> new EntityNotFoundException());
		return author.getBooks().stream()
				.map(b -> modelMapper.map(b, BookDto.class))
				.collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public Iterable<BookDto> findBooksByPublisher(String publisherName) {
//		return bookRepository.findByPublisherPublisherName(publisherName)
//				.map(b -> modelMapper.map(b, BookDto.class))
//				.collect(Collectors.toList());
		Publisher publisher = publisherRepository.findById(publisherName).orElseThrow(() -> new EntityNotFoundException());
		return publisher.getBooks().stream()
				.map(b -> modelMapper.map(b, BookDto.class))
				.collect(Collectors.toList());
	}

	@Override
//	@Transactional(readOnly = true)
	public Iterable<AuthorDto> findBookAuthors(String isbn) {
		Book book = bookRepository.findById(isbn).orElseThrow(() -> new EntityNotFoundException());
		return book.getAuthors().stream()
				.map(a -> modelMapper.map(a, AuthorDto.class))
				.collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public Iterable<String> findPublishersByAuthor(String authorName) {
//		return publisherRepository.findPublishersByAuthor(authorName);
		return publisherRepository.findDistinctByBooksAuthorsName(authorName)
					.map(p -> p.getPublisherName())
					.collect(Collectors.toSet());
	}

	@Override
	@Transactional
	public AuthorDto removeAuthor(String authorName) {
		Author author = authorRepository.findById(authorName).orElseThrow(() -> new EntityNotFoundException());
//		bookRepository.findByAuthorsName(authorName)
//						.forEach(b -> bookRepository.delete(b));
		authorRepository.delete(author);
		return modelMapper.map(author, AuthorDto.class);
	}

}
