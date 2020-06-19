## [ALGORITHM] Binary Search : 이진탐색

업다운 게임과 비슷하다.
찾으려는 값을 한가운데 값((min + max) / 2)과 비교하고
- 작을 경우 min ~ ((min + max) / 2) 까지 다시 비교
- 클 경우 ((min + max) / 2) ~ max 까지 다시 비교

첫 탐색에 대한 로직만 구현하고 이후 비교문에서는 재귀함수 방법을 사용한다.

들어오는 배열은 오름차순(sort)으로 정렬 되어있어야 한다.


### 구현체
```java
public void binarySearch(int[] ar, int start, int end, int search, int cnt) {
		int index = (start + end) / 2;
		
		if(start > end) {
			System.out.println("not found search data : " + search);
			return;
		}
		
		if(ar[index] == search) {
			System.out.println("find search data : " + search + ", position : ar[" + index + "], try count : " + (++cnt));
		}
		else if(ar[index] > search) {
			binarySearch(ar, 0, index - 1, search, ++cnt);
		}
		else if(ar[index] < search) {
			binarySearch(ar, index + 1, end, search, ++cnt);
		}
	}
```
- int[] ar : 찾으려는 데이터가 들어있는 배열 (오름차순 정렬이 되어 있어야 함.)
- start : 탐색 범위의 가장 작은 인덱스, 0과 일치한다.
- end : 탐색 범위의 가장 큰 인덱스, 전체탐색시 ar.length -1과 일치한다.
- search : 찾으려는 값
- cnt : recursive 호출 횟수, 탐색횟수와 같다.

### 사용
```java
public static void main(String[] args) {
		int[] arr = {0, 2, 4, 6, 8, 10, 12, 13, 14, 15, 16, 18, 20, 22, 24, 26, 28, 30, 32, 50, 55, 100, 200};
		new Test().binarySearch(arr, 0, arr.length - 1, 24, 0);
		new Test().binarySearch(arr, 0, arr.length - 1, 4524, 0); //없는 값
		new Test().binarySearch(arr, 0, arr.length - 1, 1, 0); //없는 값
	}
```

### Console
---
find search data : 24, position : ar[14], try count : 5
not found search data : 4524
not found search data : 1

---