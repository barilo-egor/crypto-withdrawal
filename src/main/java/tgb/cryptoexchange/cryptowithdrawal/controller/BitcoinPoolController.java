package tgb.cryptoexchange.cryptowithdrawal.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tgb.cryptoexchange.cryptowithdrawal.interfaces.IPoolDealService;
import tgb.cryptoexchange.cryptowithdrawal.po.PoolDeal;
import tgb.cryptoexchange.web.ApiResponse;

import java.util.List;

@RestController
@RequestMapping("/pool")
public class BitcoinPoolController {

    private final IPoolDealService poolDealService;

    public BitcoinPoolController(IPoolDealService poolDealService) {
        this.poolDealService = poolDealService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PoolDeal>>> getPoolDeals() {
        return new ResponseEntity<>(ApiResponse.success(poolDealService.findAll()), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Long>> add(@RequestBody PoolDeal poolDeal) {
        if (!poolDeal.isValid()) {
            return new ResponseEntity<>(
                    ApiResponse.error(ApiResponse.Error.builder().message("Не заполнены обязательные поля").build()),
                    HttpStatus.BAD_REQUEST
            );
        }
        poolDeal = poolDealService.save(poolDeal);
        return new ResponseEntity<>(ApiResponse.success(poolDeal.getId()), HttpStatus.ACCEPTED);
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Long>> delete(@RequestBody PoolDeal poolDeal) {
        Long id = poolDealService.delete(poolDeal.getId());
        return new ResponseEntity<>(ApiResponse.success(id), HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/all")
    public ResponseEntity<ApiResponse<Boolean>> deleteAll() {
        poolDealService.deleteAll();
        return new ResponseEntity<>(ApiResponse.success(true), HttpStatus.OK);
    }

    @PostMapping("/complete")
    public ResponseEntity<ApiResponse<String>> complete() {
        String hash = poolDealService.complete();
        return new ResponseEntity<>(ApiResponse.success(hash), HttpStatus.OK);
    }
}
