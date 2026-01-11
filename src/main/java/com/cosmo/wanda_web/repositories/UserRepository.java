package com.cosmo.wanda_web.repositories;

import com.cosmo.wanda_web.entities.User;
import com.cosmo.wanda_web.projections.UserDetailsProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query(nativeQuery = true, value = """
   SELECT tb_user.email AS username, tb_user.password, tb_role.id AS roleId, tb_role.authority, tb_user.profile_type AS profile
   FROM tb_user
   INNER JOIN tb_user_role ON tb_user.id = tb_user_role.user_id
   INNER JOIN tb_role ON tb_role.id = tb_user_role.role_id
   WHERE tb_user.email = :email
""")
    List<UserDetailsProjection> searchUserAndRolesByEmail(String email);

    User findByEmail(String email);

    @Query("""
           SELECT obj
           FROM User obj
           WHERE obj.id <> :currentUserId
           AND ( :q IS NULL
                OR LOWER(obj.name) LIKE :q
                OR LOWER(obj.email) LIKE :q )
           """)
    Page<User> findAllByName(@Param("q") String q,
                             @Param("currentUserId") Long currentUserId,
                             Pageable pageable);
}
