package tgb.cryptoexchange.cryptowithdrawal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tgb.cryptoexchange.cryptowithdrawal.po.PoolDeal;

@Repository
public interface PoolDealRepository extends JpaRepository<PoolDeal, Long> {

}
