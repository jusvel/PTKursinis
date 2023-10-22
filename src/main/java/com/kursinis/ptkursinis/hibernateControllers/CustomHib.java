package com.kursinis.ptkursinis.hibernateControllers;

import com.kursinis.ptkursinis.model.Customer;
import com.kursinis.ptkursinis.model.Product;
import com.kursinis.ptkursinis.model.User;
import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.ArrayList;
import java.util.List;

public class CustomHib extends GenericHib{
    public CustomHib(EntityManagerFactory entityManagerFactory) {
        super(entityManagerFactory);
    }

    public List<String> getDistinctTypes(){
        EntityManager em = null;
        try{
            em = getEntityManager();
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<String> cq = cb.createQuery(String.class);
            Root<Product> root = cq.from(Product.class);
            cq.select(root.get("class").as(String.class)).distinct(true);
            TypedQuery<String> query = em.createQuery(cq);
            return query.getResultList();
        } catch (NoResultException e) {
            return null;
        } finally {
            if (em != null) em.close();
        }
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
}
