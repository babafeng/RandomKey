Android-Java-RandomKey
======================

## 原理

### AES随机密钥

通过服务端随机生成AES加解密密钥，使用这个密钥加密传输数据（完成一个来回的数据交互）

### RSA传递随机密钥

随机生成的AES密钥使用RSA私钥加密随数据传递到客户端，客户端使用RSA公钥解密AES随机密钥

