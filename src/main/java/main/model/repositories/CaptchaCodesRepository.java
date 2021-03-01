package main.model.repositories;

import main.model.CaptchaCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CaptchaCodesRepository extends JpaRepository<CaptchaCode, Integer> {

    @Query("From CaptchaCode as cCode where cCode.secretCode = :secretCode")
    CaptchaCode getCaptchaCodeBySecretCode(@Param("secretCode") String secretCode);

}
