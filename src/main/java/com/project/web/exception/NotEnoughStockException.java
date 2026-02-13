package com.project.web.exception;

public class NotEnoughStockException extends RuntimeException {
	// 메시지("재고가 부족합니다")를 받아서 부모(RuntimeException)에게 넘겨줌
    public NotEnoughStockException(String message) {
        super(message);
    }
    
    // 필요하다면 예외 발생 시 원인(cause)도 같이 넘기는 생성자 추가 가능
    public NotEnoughStockException(String message, Throwable cause) {
        super(message, cause);
    }
}
