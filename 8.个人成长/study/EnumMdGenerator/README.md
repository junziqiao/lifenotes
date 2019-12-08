# EnumMdGenerator

#### 项目介绍
使用markdown文本来生成枚举类
  
#### 安装教程

1. idea插件栏搜索EnumMdGenerator安装
2. 自行编译后直接安装

#### 使用说明

1. 仅限idea 2018.1.3使用，其他版本可能不能兼容  如遇不能兼容请自行编译
2. create enum class
3. shift + alt + e
4. import right markdown table grammar
5. ok
6. 目前有个小问题 当这种情况时\
TEXT | 1 | null\
xxx | 2 | xxx (任意类型)\
对null 值不会做任何处理, 如果此时实际类型为非string 就会全部变为string类型\
如果有如此需求时建议 赋予null为特别实际类型的值 生成后再手动更改为null就好了\
暂时没空改了
### test data
enum_name | code | name 
-|-|-
MASTER | 1 | 硕士及以上
REGULAR_COLLEGE_COURSE | 2 | 本科

 _强调_ 注意 markdown 表格语法 最少要有3行，且第一行必须为枚举的真实名称（enum_name叫什么无所谓）

### 图文
[test markdown table data](https://gitee.com/zombie1993/EnumMdGenerator/blob/master/img/img1.png)\
[result](https://gitee.com/zombie1993/EnumMdGenerator/blob/master/img/2.png)

### 强迫症福音
idea 默认的格式化会导致枚举常量缩在一行如下配置后即可实现换行
File => Settings => Editor => Code Style => Java => Wrapping

搜索或找到enum constants 选择Wrap always