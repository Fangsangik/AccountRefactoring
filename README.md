# AccountRefactoring
외부  시스탬에서 거래를 요청하는 경우 거래를 받아서 계좌에서 잔액을 거래 금액 만큼 줄이거나 늘리는 거래 관련 기능을 제공하는 System 

- 개발환경 
    JAVA
    SpringBoot 
    H2 DB
    JPA
    Embedded redis (아직 공부가 필요함) 

구현 예정 기능 

- 사용자 
    신규 등록
    해지 
    중지
    정보조회 

- 계좌 
    계좌 추가
    해지
    확인

- 거래 
    잔액 사용
    잔액 사용 취소
    거래 확인 

그 외 기능 
  - 로그 찍기
  - Spring Security로 login 구현 해보기 


AccountUser 
- 이름, 생성일, update일
