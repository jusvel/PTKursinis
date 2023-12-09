package com.kursinis.ptkursinis.hibernateControllers;

import com.kursinis.ptkursinis.helpers.StringHelpers;
import com.kursinis.ptkursinis.model.*;
import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CustomHib<T> extends GenericHib{
    public CustomHib(EntityManagerFactory entityManagerFactory) {
        super(entityManagerFactory);
    }

    public User getUserByCredentials(String username, String password) {
        EntityManager em = null;
        try{
            em = getEntityManager();
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<User> cbQuery = cb.createQuery(User.class);
            Root<User> root= cbQuery.from(User.class);
            cbQuery.select(root).where(cb.and(
                    cb.equal(root.get("username"),username),
                    cb.equal(root.get("password"),password)
                )
            );

            Query query = em.createQuery(cbQuery);

            return (User) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            if (em != null) em.close();
        }
    }

    public boolean isUsernameAvailable(String username){
        EntityManager em = null;
        try{
            em = getEntityManager();
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<User> cbQuery = cb.createQuery(User.class);
            Root<User> root= cbQuery.from(User.class);
            cbQuery.select(root).where(cb.equal(root.get("username"),username));

            Query query = em.createQuery(cbQuery);

            return query.getResultList().isEmpty();
        } catch (NoResultException e) {
            return true;
        } finally {
            if (em != null) em.close();
        }
    }

    public boolean isEmailAvailable(String text) {
        EntityManager em = null;
        try{
            em = getEntityManager();
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<User> cbQuery = cb.createQuery(User.class);
            Root<User> root= cbQuery.from(User.class);
            cbQuery.select(root).where(cb.equal(root.get("email"),text));

            Query query = em.createQuery(cbQuery);

            return query.getResultList().isEmpty();
        } catch (NoResultException e) {
            return true;
        } finally {
            if (em != null) em.close();
        }
    }

    public void deleteFromWarehouse(Product product){
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();

            Product managedProduct = em.merge(product);
            if(managedProduct.getWarehouse() != null){
                managedProduct.getWarehouse().getProductsInWarehouse().remove(managedProduct);
            }
            em.remove(managedProduct);
            em.getTransaction().commit();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if(em != null) em.close();
        }
    }


    public List<T> getProductSearch(Class<T> entityClass, String column, String value) {
        EntityManager em = null;
        column = StringHelpers.camelCaseConverter(column);
        System.out.println(column + " " + value);

        try{
            em = getEntityManager();
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<T> cbQuery = cb.createQuery(entityClass);
            Root<T> root= cbQuery.from(entityClass);
            cbQuery.select(root).where(
                    ((column.toLowerCase().contains("price")) || (column.toLowerCase().contains("inches"))) ?
                                    cb.equal(root.get(column), Double.parseDouble(value)) :
                    ((column.toLowerCase().contains("id")) || (column.toLowerCase().contains("battery")) || (column.toLowerCase().contains("ram")) || (column.toLowerCase().contains("storage")) || (column.toLowerCase().contains("diameter")) || (column.toLowerCase().contains("weight"))) ?
                                    cb.equal(root.get(column), Integer.parseInt(value)) :
                                    cb.like(root.get(column), "%" + value + "%")
            );

            Query query = em.createQuery(cbQuery);

            return query.getResultList();
        } catch (NoResultException e) {
            return null;
        } finally {
            if (em != null) em.close();
        }
    }

    public Cart getCartByUser(User currentUser) {
        EntityManager em = null;
        try{
            em = getEntityManager();
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Cart> cbQuery = cb.createQuery(Cart.class);
            Root<Cart> root= cbQuery.from(Cart.class);
            cbQuery.select(root).where(cb.equal(root.get("user"),currentUser));

            Query query = em.createQuery(cbQuery);

            return (Cart) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            if (em != null) em.close();
        }
    }

    public List<Order> getOrdersByUser(User currentUser) {
        EntityManager em = null;
        try{
            em = getEntityManager();
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Order> cbQuery = cb.createQuery(Order.class);
            Root<Order> root= cbQuery.from(Order.class);
            cbQuery.select(root).where(cb.equal(root.get("user"),currentUser));

            Query query = em.createQuery(cbQuery);

            return query.getResultList();
        } catch (NoResultException e) {
            return null;
        } finally {
            if (em != null) em.close();
        }
    }

    public List<Order> getOrdersByFilters(String username, String responsibleEmployee, LocalDate startDate, LocalDate endDate) {
        EntityManager em = null;
        try{
            em = getEntityManager();
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Order> cbQuery = cb.createQuery(Order.class);
            Root<Order> root= cbQuery.from(Order.class);

            List<Predicate> predicates = new ArrayList<>();

            if (username != null && !username.isEmpty()) {
                predicates.add(cb.like(root.get("user").get("username"), "%" + username + "%"));
            }

            if (responsibleEmployee != null && !responsibleEmployee.isEmpty()) {
                predicates.add(cb.like(root.get("responsibleEmployee"), "%" + responsibleEmployee + "%"));
            }

            if (startDate != null && endDate != null) {
                predicates.add(cb.between(root.get("dateCreated"), startDate, endDate));
            } else if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("dateCreated"), startDate));
            } else if (endDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("dateCreated"), endDate));
            }

            cbQuery.select(root).where(predicates.toArray(new Predicate[0]));

            Query query = em.createQuery(cbQuery);

            return query.getResultList();
        } catch (NoResultException e) {
            return null;
        } finally {
            if (em != null) em.close();
        }
    }

    public List<CoolReview> getCoolReviewsByProductId(int productId) {
        EntityManager em = null;
        try{
            em = getEntityManager();
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<CoolReview> cbQuery = cb.createQuery(CoolReview.class);
            Root<CoolReview> root= cbQuery.from(CoolReview.class);
            cbQuery.select(root).where(cb.equal(root.get("product").get("id"),productId));

            Query query = em.createQuery(cbQuery);

            return query.getResultList();
        } catch (NoResultException e) {
            return null;
        } finally {
            if (em != null) em.close();
        }
    }

    public void deleteCoolReview(CoolReview selectedReview) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();

            CoolReview managedReview = em.merge(selectedReview);
//            if(managedReview.getParentId() != null){
                managedReview.getProduct().getCoolReviews().remove(managedReview);
//            }
            em.remove(managedReview);
            em.getTransaction().commit();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if(em != null) em.close();
        }
    }
}
