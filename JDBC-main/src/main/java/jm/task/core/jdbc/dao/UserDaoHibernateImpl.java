package jm.task.core.jdbc.dao;

import jm.task.core.jdbc.model.User;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

import static jm.task.core.jdbc.util.Util.getSessionFactory;

public class UserDaoHibernateImpl implements UserDao {
    // В хорошем коде sessionFactory должен быть полем класса,
    // но в рамках задачи его берут из Util.getSessionFactory().

    public UserDaoHibernateImpl() {
        // Конструктор
    }

    //--------------------------------------------------------------
    // 1. Создание таблицы (Используем Нативный SQL)
    //--------------------------------------------------------------
    @Override
    public void createUsersTable() {
        Transaction transaction = null;
        try (Session session = getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            String sql = "CREATE TABLE IF NOT EXISTS users " +
                    "(id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                    "name VARCHAR(50) NOT NULL, lastName VARCHAR(50) NOT NULL, " +
                    "age TINYINT NOT NULL)";

            // Для DDL (CREATE/DROP) просто выполняем SQL-запрос
            session.createSQLQuery(sql).executeUpdate();

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    //--------------------------------------------------------------
    // 2. Удаление таблицы (Используем Нативный SQL)
    //--------------------------------------------------------------
    @Override
    public void dropUsersTable() {
        Transaction transaction = null;
        try (Session session = getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            String sql = "DROP TABLE IF EXISTS users";

            // Для DDL (CREATE/DROP) просто выполняем SQL-запрос
            session.createSQLQuery(sql).executeUpdate();

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    //--------------------------------------------------------------
    // 3. Сохранение пользователя (Используем session.save())
    //--------------------------------------------------------------
    @Override
    public void saveUser(String name, String lastName, byte age) {
        Transaction transaction = null;
        try (Session session = getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            User user = new User(name, lastName, age);

            // ПРАВИЛЬНЫЙ способ: используем метод save() для объекта User
            session.save(user);

            transaction.commit();
            System.out.printf("User с именем – %s добавлен в базу данных\n", name);
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    //--------------------------------------------------------------
    // 4. Удаление пользователя по id (Используем HQL или session.delete())
    //--------------------------------------------------------------
    @Override
    public void removeUserById(long id) {
        Transaction transaction = null;
        try (Session session = getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            // Используем HQL (Hibernate Query Language)
            Query query = session.createQuery("delete from User where id = :id");
            query.setParameter("id", id);

            // Выполняем запрос на удаление
            query.executeUpdate();

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    //--------------------------------------------------------------
    // 5. Получение всех пользователей (Используем HQL)
    //--------------------------------------------------------------
    @Override
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        Transaction transaction = null;
        try (Session session = getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            // Используем HQL для получения всех объектов User
            users = session.createQuery("from User", User.class).list();

            transaction.commit();

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            // В случае ошибки возвращаем пустой список, а не null (как обсуждалось ранее)
            return new ArrayList<>();
        }
        return users;
    }

    //--------------------------------------------------------------
    // 6. Очистка таблицы (Используем Нативный SQL)
    //--------------------------------------------------------------
    @Override
    public void cleanUsersTable() {
        Transaction transaction = null;
        try (Session session = getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            // **Используйте HQL для удаления всех записей:**
            session.createQuery("delete from User").executeUpdate();

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }
}