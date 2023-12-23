package com.smart.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.smart.entites.Contact;
@Repository
public interface ContactRepository extends JpaRepository<Contact, Integer>{
	//pagination
	
	@Query("from Contact as c where c.user.id =:userId")
	
	// this is for list of contacts 
//	public List<Contact> findContactByUser(@Param("userId") int userId);
	// this is for n no of contacts in one page
	//current page =0th
	// per page 5 contacts
	public Page<Contact> findContactByUser(@Param("userId") int userId,Pageable pageable);

}
