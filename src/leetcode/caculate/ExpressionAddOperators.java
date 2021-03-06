package leetcode.caculate;

import java.util.ArrayList;
import java.util.List;

//https://leetcode.com/problems/expression-add-operators/
/*
因為要輸出所有可能的情況，必定是用深度優先搜索。問題在於如何將問題拆分成多次搜索。

加減法很好處理，每當我們截出一段數字時，將之前計算的結果加上或者減去這個數，
就可以將剩餘的數字字符串和新的計算結果代入下一次搜索中了，直到我們的計算結果和目標一樣，
就完成了一次搜索。

然而，乘法如何處理呢？這裡我們需要用一個變量記錄乘法當前累乘的值，
直到累乘完了，遇到下一個加號或減號再將其算入計算結果中。這裡有兩種情況:

乘號之前是加號或減號，例如2+3*4，我們在2那裡算出來的結果，到3的時候會加上3，計算結果變為5。在到4的時候，
因為4之前我們選擇的是乘號，這裡3就應該和4相乘，而不是和2相加，所以在計算結果時，要將5先減去剛才加的3得到2 ，
然後再加上3乘以4，得到2+12=14，這樣14就是到4為止時的計算結果。

另外一種情況是乘號之前也是乘號，如果2+3*4*5，這裡我們到4為止計算的結果是14了，
然後我們到5的時候又是乘號，這時候我們要把剛才加的3*4給去掉，然後再加上3*4*5，也就是14-3*4+3*4*5=62。
這樣5的計算結果就是62。

因為要解決上述幾種情況，我們需要這麼幾個變量，
一個是記錄上次的計算結果 currRes，
一個是記錄上次被加或者被減的數 prevNum，
一個是當前準備處理的數 currNum。

當下一輪搜索是加減法時， prevNum就是簡單換成currNum，
當下一輪搜索是乘法時，prevNum是prevNum乘以currNum。


第一次搜索不添加運算符，只添加數字，就不會出現+1+2這種表達式了。

我們截出的數字不能包含0001這種前面有0的數字，但是一個0是可以的。這裡一旦截出的數字前導為0，
就可以return了，因為說明前面就截的不對，從這之後都是開始為0的，後面也不可能了。

Input: num = "123", target = 6
Output: ["1+2+3", "1*2*3"]

Input: num = "232", target = 8
Output: ["2*3+2", "2+3*2"]

the problem is in multiply, so when 2+3*2, process until 3, 2+3 = 5, but then multiply,
so need to do 5-3 => ( last-Result - prev operation's value) then do prev operation's value * current number

so we need to record last result, last operation's number,

when mutiplication:
result =  (lastResult - preNum) + (preNum*currentNum), operationNum(preNum) = preNum*currentNum

when add:
result =  lastResult + currentNum, operationNum(preNum) = currentNum

when subtraction:
result =  lastResult - currentNum, operationNum(preNum) = -currentNum



helper (num, target, resStr, res, preNum)
helper (要處理的 num 字串, target, 最後結果字串, 上次的計算結果(long), 上次被加或者被減的數(long))

use long to avoid overflow

if (currRes == target && num.length() == 0) {
    add result
}

one word one word pass

String currStr = num.substring(0, i);
// conrner case: 0001
if (currStr.length() > 1 && currStr.charAt(0) == '0') {

                // 這裡是return不是continue
                return;
}

long currNum = Long.parseLong(currStr);
String next = num.substring(i);


if (resStr.length() != 0) {

dfs
//multiply
//add
//subtract

} else { //first word
}

T(n) = 3 * T(n-1) + 3 * T(n-2) + 3 * T(n-3) + ... + 3 *T(1);
T(n-1) = 3 * T(n-2) + 3 * T(n-3) + ... 3 * T(1);
Thus T(n) = 4T(n-1);
The homogeneous recurrence is T(n)=4T(n−1), which has general solution T(n)=A4n.
O(4^N)
O(N)
*/

public class ExpressionAddOperators {
    List<String> res = new ArrayList<>();

    public List<String> addOperators(String num, int target) {
        helper(num, target, "", 0, 0);
        return res;
    }

    private void helper(String num, int target, String resStr, long currRes, long prevNum) {
        // 如果計算結果等於目標值，且所有數都用完了，則是有效結果
        if (currRes == target && num.length() == 0) { //!!! 在最外圈！ ！ ！
            String exp = new String(resStr);
            res.add(exp);
            return; /// ！ ！ ！ ！ end!
        }
        // 搜索所有可能的拆分情況
        for (int i = 1; i <= num.length(); i++) { //!!! i form 1 ro num end
            String currStr = num.substring(0, i);
            // conrner case: 對於前導為0的數予以排除
            if (currStr.length() > 1 && currStr.charAt(0) == '0') { //!!! 沒有currStr.length() > 1 && 的話 105 target 5 的答案 1*0 + 5, 會失敗, 0出不來

                // 這裡是return不是continue
                return;
            }
            // 得到當前截出的數
            long currNum = Long.parseLong(currStr);
            // 去掉當前的數，得到下一輪搜索用的字符串
            String next = num.substring(i);
            // 如果不是第一個字母時，可以加運算符，否則只加數字
            if (resStr.length() != 0) {
                // 乘法
                helper(next, target, resStr + "*" + currNum, (currRes - prevNum) + prevNum * currNum, prevNum * currNum);
                // 加法
                helper(next, target, resStr + "+" + currNum, currRes + currNum, currNum);
                // 減法
                helper(next, target, resStr + "-" + currNum, currRes - currNum, -currNum);
            } else {
                // 第一個數
                helper(next, target, currStr, currNum, currNum);
            }

        }
    }

    public static void main(String args[]) {
        /*
            Input: num = "123", target = 6
            Output: ["1+2+3", "1*2*3"]

            Input: num = "00", target = 0
            Output: ["0+0", "0-0", "0*0"]
         */

        String num = "123";
        int target = 6;

        List<String> res = new ExpressionAddOperators().addOperators(num, target);
        res.stream().forEach(s -> System.out.println(s));

//        String num2 = "00";
//        int target2 = 0;
//
//        List<String> res2 = new ExpressionAddOperators().addOperators(num2, target2);
//        res2.stream().forEach(s -> System.out.println(s));
    }
}
