package xdi2.example.server.contributor.database;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BookDao implements Serializable {

	private static final long serialVersionUID = -5541425794859656552L;

	private static final Logger log = LoggerFactory.getLogger(BookDao.class);

	private static Connection connection;

	private long id;
	private String title;
	private String author;
	private String publisher;
	private String country;
	private int year;
	private float price;

	public static void init() throws Exception {

		Class.forName("com.mysql.jdbc.Driver");
		connection = DriverManager.getConnection("jdbc:mysql://localhost/books?user=books&password=books");
	}

	public static void shutdown() throws Exception {

		connection.close();
		connection = null;
	}

	private static BookDao create(ResultSet resultSet) throws SQLException {

		BookDao book = new BookDao();
		book.setId(resultSet.getInt("id"));
		book.setTitle(resultSet.getString("title"));
		book.setAuthor(resultSet.getString("author"));
		book.setPublisher(resultSet.getString("publisher"));
		book.setCountry(resultSet.getString("country"));
		book.setYear(resultSet.getInt("year"));
		book.setPrice(resultSet.getFloat("price"));

		return book;
	}

	public static BookDao get(long id) throws SQLException {

		log.info("get " + id);

		PreparedStatement statement = connection.prepareStatement("select * from books where id=?");
		statement.setLong(1, id);
		ResultSet resultSet = statement.executeQuery();
		if (! resultSet.next()) return null;

		return create(resultSet);
	}

	public static List<BookDao> list() throws SQLException {

		log.info("list");

		Statement statement = connection.createStatement();
		ResultSet resultSet = statement.executeQuery("select * from books");

		List<BookDao> list = new ArrayList<BookDao> ();
		while (resultSet.next()) list.add(create(resultSet));

		return list;
	}

	public static void save(BookDao book) throws SQLException {

		log.info("save " + book.getId());

		PreparedStatement statement = connection.prepareStatement("update books set title=?,author=?,publisher=?,country=?,year=?,price=? where id=?");
		statement.setString(1, book.getTitle());
		statement.setString(2, book.getAuthor());
		statement.setString(3, book.getPublisher());
		statement.setString(4, book.getCountry());
		statement.setInt(5, book.getYear());
		statement.setFloat(6, book.getPrice());
		statement.setLong(7, book.getId());

		statement.executeUpdate();
	}

	public long getId() {

		return id;
	}

	public void setId(long id) {

		this.id = id;
	}

	public String getTitle() {

		return title;
	}

	public void setTitle(String title) {

		this.title = title;
	}

	public String getAuthor() {

		return author;
	}

	public void setAuthor(String author) {

		this.author = author;
	}

	public String getPublisher() {

		return publisher;
	}

	public void setPublisher(String publisher) {

		this.publisher = publisher;
	}

	public String getCountry() {

		return country;
	}

	public void setCountry(String country) {

		this.country = country;
	}

	public int getYear() {

		return year;
	}

	public void setYear(int year) {

		this.year = year;
	}

	public float getPrice() {

		return price;
	}

	public void setPrice(float price) {

		this.price = price;
	}
}
