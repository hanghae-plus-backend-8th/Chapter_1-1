## 🛠 Java 동기화 메커니즘

- Java에서 제공되는 수많은 동기화 메커니즘이 있지만 이번 과제에서는 Synchronized, ReentantLock 방식에 대해 알아보았습니다.

### (1) Synchronized

```java
synchronized UserPoint chargePoint(long userId, long amount) {
    UserPoint userPoint = userPointTable.selectById(userId);
    long chargedPoint = pointManager.chargePoint(userPoint.point(), amount);

    userPoint = userPointTable.insertOrUpdate(userId, chargedPoint);
    pointHistoryTable.insert(userId, amount, TransactionType.CHARGE, userPoint.updateMillis());

    return userPoint;
}
```
- 장점 

1. 메소드에 간편하게 "synchronized" 키워드를 설정함으로서 쉽게 동시성 제어가 가능합니다!

- 단점 

1. 모든 요청을 "직렬"로 처리하기 때문에 성능이 느립니다!!! (e.g. DB isolation - Serializable / Global Lock) 

2. Timeout을 설정할 수 없기 때문에 다른 Synchronized 메소드와 연계해서 쓰는 경우, 데드락이 발생할 가능성이 있어 "잘" 사용해야 합니다.

---

### (2) ReentrantLock + ConcurrentHashMap

```java
private final Map<Long, ReentrantLock> keyMap = new ConcurrentHashMap<>();

public UserPoint chargePoint(long userId, long amount) {
    ReentrantLock lock = keyMap.computeIfAbsent(userId, k -> new ReentrantLock());
    lock.lock();

    try {
        UserPoint userPoint = userPointTable.selectById(userId);
        long chargedPoint = pointManager.chargePoint(userPoint.point(), amount);

        userPoint = userPointTable.insertOrUpdate(userId, chargedPoint);
        pointHistoryTable.insert(userId, amount, TransactionType.CHARGE, userPoint.updateMillis());

        return userPoint;
    } finally {
        lock.unlock();
    }
}
```
- 장점

1. ConcurrentHashMap 활용 시, 사용자별로 Lock을 관리하여 병렬로 처리되기 때문에 성능이 좋습니다!!! (e.g. DB row Lock)

2. 명시적인 Lock 설정이 가능하기 때문에 세밀한 제어가 가능합니다! (필요한 부분에서 Lock 설정)

3. 타임아웃을 설정하여 데드락을 방지할 수 있습니다! --> e.g. tryLock(timeout)

- 단점

1. ReentrantLock만을 사용하게 되면 Synchronized와 동일하게 직렬로 처리됩니다.

2. 자동으로 Lock을 해제하지 않기 때문에 누락하게 되면 대기중인 스레드가 Busy-Waiting 현상이 발생할 수 있습니다!

3. 간결하게 사용할 수 있는 Synchronized 키워드와 달리 코드가 복잡해지는 단점이 있습니다.





