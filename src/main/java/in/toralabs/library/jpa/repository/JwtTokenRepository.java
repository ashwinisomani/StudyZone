package in.toralabs.library.jpa.repository;

import in.toralabs.library.jpa.model.JwtTokenModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface JwtTokenRepository extends JpaRepository<JwtTokenModel, String> {

    @Modifying
    @Transactional
    @Query(value = "update lib_jwt_tokens set enabled = false where token_id = ?1", nativeQuery = true)
    void disableJwtTokenId(@Param("token_id") String jwtTokenId);

    @Modifying
    @Transactional
    @Query(value = "update lib_jwt_tokens set enabled = false", nativeQuery = true)
    void disableAllJwtTokens();

    @Modifying
    @Transactional
    @Query(value = "delete from lib_jwt_tokens where creation_date < current_date - 2", nativeQuery = true)
    void deleteAllJwtTokensCreatedBeforeAFewDays();

    @Query(value = "select * from lib_jwt_tokens where enabled = true", nativeQuery = true)
    List<JwtTokenModel> findAllActiveTokens();
}
