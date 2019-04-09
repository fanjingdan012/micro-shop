package com.fjd;

import com.fjd.model.User;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class OptimisticLockExceptionExample {
  private static EntityManagerFactory entityManagerFactory =
          Persistence.createEntityManagerFactory("example-unit");

  public static void main(String[] args) {
      ExecutorService es = Executors.newFixedThreadPool(2);
      try {
          persistEmployee();
          es.execute(() -> {
              try {
                  updateEmployee1();
              } catch (Exception e) {
                  System.out.println("-- exception thrown during update 1 --");
                  e.printStackTrace();
              }
          });
          es.execute(() -> {
              try {
                  updateEmployee2();
              } catch (Exception e) {
                  System.out.println("-- exception thrown during update 2 --");
                  e.printStackTrace();
              }
          });
          es.shutdown();
          //wait for the threads to finish
          try {
              es.awaitTermination(5, TimeUnit.SECONDS);
          } catch (InterruptedException e) {
              throw new RuntimeException(e);
          }
          loadEmployee();

      } finally {

          entityManagerFactory.close();
      }
  }

  private static void updateEmployee1() {
      System.out.println("Update 1 starts, changing dept to Sales");
      EntityManager em = entityManagerFactory.createEntityManager();
      User employee = em.find(User.class, 1);
      em.getTransaction().begin();
      System.out.println("Lock Mode for update 1: " + em.getLockMode(employee));
      employee.setName("Sales");
      try {
          System.out.println("Pausing first transaction for 1 second");
          //wait for 1 sec before commit
          Thread.sleep(1000);
      } catch (InterruptedException e) {
          throw new RuntimeException(e);
      }
      System.out.println("committing first transaction");
      em.getTransaction().commit();
      em.close();
      System.out.println("Employee updated 1: " + employee);
  }

  private static void updateEmployee2() {
      System.out.println("Update 2 starts, changing dept to Admin");
      EntityManager em = entityManagerFactory.createEntityManager();
      User employee = em.find(User.class, 1);
      em.getTransaction().begin();
      System.out.println("Lock Mode for update 2: " + em.getLockMode(employee));
      employee.setName("Admin");
      em.getTransaction().commit();
      em.close();
      System.out.println("Employee updated 2: " + employee);
  }

  private static void loadEmployee() {
      EntityManager em = entityManagerFactory.createEntityManager();
      User employee = em.find(User.class, 1);
      System.out.println("Employee loaded: " + employee);
  }

  public static void persistEmployee() {
      User employee = new User();
      employee.setName("Joe");
      EntityManager em = entityManagerFactory.createEntityManager();
      em.getTransaction().begin();
      em.persist(employee);
      em.getTransaction().commit();
      em.close();
      System.out.println("Employee persisted: " + employee);
  }
}