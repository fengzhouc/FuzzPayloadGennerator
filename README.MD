# Intruder Payload Generator
主要是解决我日常爆破时字典的需要，收集的字典数据太多了，有时想要指定特征的爆破字典就要自己手撸，
所以就想着写个插件了，可以自己通过配置生成字典了，
未来会持续写，把日常可能使用到的都实现了，主要是太烂了。
## Password
密码字典生成器，先看个配置界面吧
![img.png](img.png)
配置解读
- 1.密码长度要求：不能理解我也办法了
- 2.密码复杂度：指定复杂度的范围，一般密码要求就是"字母/数字/特殊字符的两种及以上组合"，那范围就是2～3
- 3.密码复杂度选择：打开密码复杂度的检测开关，如果不开，但又要这部分数据的话，你就yy吧
- 4.基础数据集选择：也就是参与组合的基础数据，比如我就想要大写/小写字母，那就勾选，然后组合就会拿这两个数据集进行排列组合
- 4.1 MinLen：上面基础数据集数据的最小长度，比如大写字母，MinLen为2，那生成的就是ab/bcd/cdef等，最小长度是2
- 4.2 常见用户特征：这里会内置一些常见的特征，用于生成密码，很常见的就是'admin@123'，可新增删除
- 4.3 自定义组合：就是字面意思自定义，比如我想生成admin@123这样格式的密码，那就根据基础数据集的index进行组合（index在每类数据集的末尾）
- 5.按钮Genrate：根据配置生成密码字典，但是生成的结果会追加到末尾
- 6.按钮Regenrate：就翻译意思，重新生成，相比Genrate，会清空已有的数据
- 7.展示生成的数据
- 7.1按钮Save：保存数据到文件
- 7.2按钮emove/Clear，就是删除选中及清空数据

## 注意
生成预期尽量精确，因为生成太多数据的话，内存会撑不住的，然后Boom哇～

# 未来规划
## Username
提供用户名的字典
## Host
提供域名字典
## Api
提供api的字典