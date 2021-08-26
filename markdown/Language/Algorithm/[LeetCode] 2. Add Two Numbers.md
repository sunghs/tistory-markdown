# Add Two Number

## 문제
```
You are given two non-empty linked lists representing two non-negative integers. 

The digits are stored in reverse order, and each of their nodes contains a single digit. Add the two numbers and return the sum as a linked list.

You may assume the two numbers do not contain any leading zero, except the number 0 itself.

Input: l1 = [2,4,3], l2 = [5,6,4]
Output: [7,0,8]
Explanation: 342 + 465 = 807.



Input: l1 = [0], l2 = [0]
Output: [0]



Input: l1 = [9,9,9,9,9,9,9], l2 = [9,9,9,9]
Output: [8,9,9,9,0,0,0,1]
```

두개의 배열의 각 자리를 합해서 결과물을 만들고, 그걸 역방향으로 숫자를 만들면 됩니다.

즉 1번 배열이 2, 4, 3 이고 2번 배열이 5, 6, 4 이므로 각 자리를 더하면 7, 10, 7 이 됩니다. 

두번째 인덱스의 10을 10진법으로 변환해서 다음 자릿수를 올림처리 하는 방식입니다.

즉 7, 0, 8이 됩니다.

이게 배열로 들어있으면 좋은데, 주어지는 값은 LinkedList의 Node 형태로 주어집니다. 따라서 Node를 동시에 탐색하며 각 자리를 더하고, 올림처리가 있다면 다음 Node 탐색 시 한자릿수를 올려주면 됩니다.

## 풀이
~~소스가 조금 지저분 합니다.~~
```java
/**
 * Definition for singly-linked list.
 * public class ListNode {
 *     int val;
 *     ListNode next;
 *     ListNode() {}
 *     ListNode(int val) { this.val = val; }
 *     ListNode(int val, ListNode next) { this.val = val; this.next = next; }
 * }
 */
class Solution {

    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        ListNode result = null;
        ListNode head = result;
        boolean upper = false;
        while (l1 != null || l2 != null) {
            int d = ((l1 == null) ? 0 : l1.val) + ((l2 == null) ? 0 : l2.val);
            if (upper) {
                d++;
                upper = false;
            }

            ListNode tmp = new ListNode();
            if (d >= 10) {
                upper = true;
                d = d % 10;
            }
            tmp.val = d;

            if (result == null) {
                result = tmp;
                head = result;
            } else {
                result.next = tmp;
                result = result.next;
            }

            if (l1 != null) {
                l1 = l1.next;
            }
            if (l2 != null) {
                l2 = l2.next;
            }
        }

        if (upper) {
            result.next = new ListNode(1);
        }
        return head;
    }
}
```

위에서 말씀드린 것과 같이 각 노드를 탐색하며 더하고, 10이 넘는다면 10으로 나눈 뒤, 다음 탐색 시 값을 하나 올려주면 됩니다.
