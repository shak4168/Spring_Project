package com.project.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * 전역 예외 처리기
 * - 컨트롤러 전역에서 발생하는 예외를 여기서 잡아(Catch) 처리합니다.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * [재고 부족 예외 처리]
     * Item.removeStock()에서 throw한 NotEnoughStockException을 잡습니다.
     */
    @ExceptionHandler(NotEnoughStockException.class)
    public ResponseEntity<Map<String, String>> handleNotEnoughStockException(NotEnoughStockException e) {
        Map<String, String> errorBody = new HashMap<>();
        errorBody.put("error", "SoldOut");
        errorBody.put("message", e.getMessage()); // "재고가 부족합니다."

        // 400 Bad Request 반환
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorBody);
    }

    /**
     * [동시성 이슈 처리]
     * @Version 충돌 시 JPA가 던지는 ObjectOptimisticLockingFailureException을 잡습니다.
     */
    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<Map<String, String>> handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e) {
        Map<String, String> errorBody = new HashMap<>();
        errorBody.put("error", "ConcurrencyFailure");
        errorBody.put("message", "주문량이 많아 처리가 지연되었습니다. 다시 시도해주세요.");

        // 409 Conflict (충돌) 반환 -> 프론트엔드에서 이 코드를 보고 '재시도' 버튼을 띄울 수 있음
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorBody);
    }
}