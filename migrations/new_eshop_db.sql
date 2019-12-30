/*
Navicat MySQL Data Transfer

Source Server         : 192.168.1.2
Source Server Version : 50550
Source Host           : 192.168.1.2:3306
Source Database       : new_eshop_db

Target Server Type    : MYSQL
Target Server Version : 50550
File Encoding         : 65001

Date: 2017-11-27 18:21:06
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for back
-- ----------------------------
DROP TABLE IF EXISTS `back`;
CREATE TABLE `back` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `customer_id` int(11) NOT NULL,
  `product_order_id` int(11) DEFAULT NULL,
  `status` tinyint(11) NOT NULL COMMENT '0进行中；1审核通过；2审核不通过；3已收货；4已退款',
  `created_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updated_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `refundAmount` int(11) DEFAULT NULL,
  `refundCash` decimal(11,2) NOT NULL,
  `isGeted` tinyint(1) DEFAULT NULL,
  `reason` text COLLATE utf8_unicode_ci,
  `note` text COLLATE utf8_unicode_ci,
  `pics` varchar(200) COLLATE utf8_unicode_ci DEFAULT NULL,
  `refuseNote` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `shop_id` int(11) NOT NULL DEFAULT '0',
  `refund_code` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `remitTime` datetime DEFAULT NULL COMMENT '打款时间',
  `successTime` datetime DEFAULT NULL COMMENT '退款成功时间',
  `operator` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '操作人姓名',
  `applyCash` decimal(10,2) NOT NULL COMMENT '退款申请金额',
  `deliveryPrice` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '邮费',
  `couponDiscount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '退款时减去的优惠金额',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=79 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- ----------------------------
-- Records of back
-- ----------------------------

-- ----------------------------
-- Table structure for category
-- ----------------------------
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  `parent_id` int(11) DEFAULT NULL,
  `sortNumber` int(11) NOT NULL DEFAULT '0',
  `note` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updated_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `mainPic` int(11) NOT NULL DEFAULT '0',
  `isDelete` smallint(6) NOT NULL DEFAULT '0' COMMENT '是否删除，0未删除，1已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=252 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- ----------------------------
-- Records of category
-- ----------------------------

-- ----------------------------
-- Table structure for customer
-- ----------------------------
DROP TABLE IF EXISTS `customer`;
CREATE TABLE `customer` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `gender` tinyint(4) DEFAULT '0',
  `dob` date DEFAULT NULL,
  `password` varchar(32) COLLATE utf8_unicode_ci DEFAULT NULL,
  `mobilePhone` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `weiXinOpenId` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `qqOpenId` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updated_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `headImg` varchar(200) COLLATE utf8_unicode_ci DEFAULT NULL,
  `note` text COLLATE utf8_unicode_ci,
  `nickName` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `disable` int(4) NOT NULL DEFAULT '0' COMMENT '0启用，1禁用，2未激活',
  `email` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `shop_id` int(11) NOT NULL DEFAULT '0',
  `unionid` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1869 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='会员表';

-- ----------------------------
-- Records of customer
-- ----------------------------

-- ----------------------------
-- Table structure for order
-- ----------------------------
DROP TABLE IF EXISTS `order`;
CREATE TABLE `order` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `customer_id` int(11) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `source` tinyint(4) NOT NULL DEFAULT '1' COMMENT '订单来源，1pc、2微信端、3android、4苹果',
  `preferredContactPhone` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `totalPayable` decimal(8,2) NOT NULL COMMENT '订单总金额，包含运费',
  `orderTime` datetime DEFAULT NULL,
  `completeTime` datetime DEFAULT NULL,
  `note` varchar(200) COLLATE utf8_unicode_ci DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updated_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `order_code` varchar(128) COLLATE utf8_unicode_ci DEFAULT NULL,
  `payType` int(11) DEFAULT NULL COMMENT '1: 微信支付, 2: 支付宝, 3钱包支付',
  `receiveDetailAddress` varchar(512) COLLATE utf8_unicode_ci DEFAULT NULL,
  `cancel_reason` varchar(512) COLLATE utf8_unicode_ci DEFAULT NULL,
  `cancel_note` text COLLATE utf8_unicode_ci,
  `shop_id` int(11) NOT NULL DEFAULT '0',
  `deliveryPrice` decimal(8,2) DEFAULT NULL,
  `expressCode` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '快递单号',
  `theSameOrderNum` int(11) DEFAULT NULL COMMENT '订单统一编号',
  `logisticsName` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '快递公司名称',
  `receiveName` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '收货人姓名',
  `receiveProvince` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `receiveCity` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `receiveDistrict` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `tradeNo` varchar(128) COLLATE utf8_unicode_ci DEFAULT NULL,
  `codeType` tinyint(4) NOT NULL DEFAULT '1' COMMENT '当前有效的订单号的类型，1同一批订单(对应theSameOrderNum)，2单独一个订单(对应order_code)',
  `payTime` datetime DEFAULT NULL COMMENT '付款时间',
  `sendOutTime` datetime DEFAULT NULL COMMENT '发货时间',
  `receiveTime` datetime DEFAULT NULL COMMENT '收货时间',
  `cancelTime` datetime DEFAULT NULL COMMENT '取消订单时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4153 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='订单表';

-- ----------------------------
-- Records of order
-- ----------------------------

-- ----------------------------
-- Table structure for product
-- ----------------------------
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `supplier_id` int(11) DEFAULT NULL,
  `upc` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `category_id` int(11) DEFAULT NULL,
  `unitCost` decimal(8,2) DEFAULT NULL,
  `suggestedRetailUnitPrice` decimal(8,2) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updated_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `is_sale` tinyint(4) NOT NULL COMMENT '0下架，1上架',
  `summary` text COLLATE utf8_unicode_ci,
  `note` text COLLATE utf8_unicode_ci,
  `mainPic` int(11) DEFAULT NULL,
  `storeAmount` int(11) NOT NULL DEFAULT '0',
  `shop_id` int(11) NOT NULL DEFAULT '0' COMMENT '0表示适用于所有店铺',
  `weight` decimal(10,2) DEFAULT NULL,
  `volume` decimal(10,2) DEFAULT NULL,
  `isSupportReturn` smallint(6) NOT NULL DEFAULT '0' COMMENT '是否支持7天包退换，0不支持，1支持',
  `originUnitPrice` decimal(8,2) DEFAULT NULL COMMENT '原价',
  `reviewNum1` int(11) DEFAULT NULL,
  `reviewNum2` int(11) DEFAULT NULL,
  `reviewNum3` int(11) DEFAULT NULL,
  `isDelete` smallint(6) NOT NULL DEFAULT '0' COMMENT '是否删除，0未删除，1已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=642 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- ----------------------------
-- Records of product
-- ----------------------------

-- ----------------------------
-- Table structure for product_order
-- ----------------------------
DROP TABLE IF EXISTS `product_order`;
CREATE TABLE `product_order` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `order_id` int(11) NOT NULL,
  `product_id` int(11) NOT NULL,
  `unitOrdered` int(11) DEFAULT NULL,
  `unitCost` decimal(8,2) DEFAULT NULL,
  `suggestedRetailUnitPrice` decimal(8,2) DEFAULT NULL,
  `actualUnitPrice` decimal(8,2) DEFAULT NULL,
  `totalProductCost` decimal(8,2) DEFAULT NULL,
  `totalDeliveryCost` decimal(8,2) DEFAULT NULL,
  `totalCost` decimal(8,2) DEFAULT NULL,
  `totalActualProductPrice` decimal(8,2) DEFAULT NULL,
  `totalActualDeliveryCharge` decimal(8,2) DEFAULT NULL,
  `totalPrice` decimal(8,2) DEFAULT NULL,
  `selectProterties` varchar(200) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '属性名和属性值得组合，格式：属性名:属性值,属性名:属性值,...',
  `status` int(4) NOT NULL DEFAULT '1' COMMENT '状态(1正常，2申请退款，3不通过，4退款中，5退款成功，6已退货, 7已评价，8退款失败)',
  `created_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updated_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `product_name` varchar(512) COLLATE utf8_unicode_ci DEFAULT NULL,
  `supplier_name` varchar(512) COLLATE utf8_unicode_ci DEFAULT NULL,
  `upc` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `category_name` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `product_summary` varchar(512) COLLATE utf8_unicode_ci DEFAULT NULL,
  `priceId` int(11) NOT NULL DEFAULT '0',
  `supplier_id` int(11) NOT NULL COMMENT '供应商id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3888 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='订单明细表';

-- ----------------------------
-- Records of product_order
-- ----------------------------

-- ----------------------------
-- Table structure for product_price
-- ----------------------------
DROP TABLE IF EXISTS `product_price`;
CREATE TABLE `product_price` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `product_id` int(11) NOT NULL,
  `type_attr` text COLLATE utf8_unicode_ci NOT NULL COMMENT '属性值id组合，格式：1,2,...',
  `price` decimal(8,2) DEFAULT NULL COMMENT '促销价',
  `weight` double(11,2) DEFAULT NULL,
  `product_number` int(11) NOT NULL DEFAULT '0',
  `alert_number` int(11) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updated_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `originUnitPrice` decimal(8,2) DEFAULT NULL COMMENT '原价',
  `unitCost` decimal(8,2) DEFAULT NULL COMMENT '成本价',
  PRIMARY KEY (`id`),
  KEY `IDX_GOODS_ID` (`product_id`)
) ENGINE=InnoDB AUTO_INCREMENT=14493 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- ----------------------------
-- Records of product_price
-- ----------------------------

-- ----------------------------
-- Table structure for product_review
-- ----------------------------
DROP TABLE IF EXISTS `product_review`;
CREATE TABLE `product_review` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `product_id` int(11) DEFAULT NULL,
  `customer_id` int(11) DEFAULT NULL,
  `ratings` int(11) DEFAULT NULL,
  `comments` varchar(500) COLLATE utf8_unicode_ci DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updated_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=515 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='商品评论表';

-- ----------------------------
-- Records of product_review
-- ----------------------------

-- ----------------------------
-- Table structure for property
-- ----------------------------
DROP TABLE IF EXISTS `property`;
CREATE TABLE `property` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  `category_id` int(11) NOT NULL,
  `sortNumber` int(11) NOT NULL DEFAULT '0',
  `created_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updated_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `is_sale_pro` int(4) NOT NULL DEFAULT '0',
  `parent_id` int(11) NOT NULL COMMENT '0表示顶级',
  `isDelete` smallint(6) NOT NULL DEFAULT '0' COMMENT '是否删除，0未删除，1已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=50 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- ----------------------------
-- Records of property
-- ----------------------------

-- ----------------------------
-- Table structure for property_value
-- ----------------------------
DROP TABLE IF EXISTS `property_value`;
CREATE TABLE `property_value` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  `property_id` int(11) NOT NULL,
  `sortNumber` int(11) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updated_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `isDelete` smallint(6) NOT NULL DEFAULT '0' COMMENT '是否删除，0未删除，1已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=94 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- ----------------------------
-- Records of property_value
-- ----------------------------

-- ----------------------------
-- Table structure for refund
-- ----------------------------
DROP TABLE IF EXISTS `refund`;
CREATE TABLE `refund` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `customer_id` int(11) DEFAULT NULL,
  `status` tinyint(4) DEFAULT NULL COMMENT '0进行中；1审核通过；2审核不通过；3确认已退款',
  `refundAmount` int(11) DEFAULT NULL,
  `refundTime` datetime DEFAULT NULL,
  `note` varchar(200) COLLATE utf8_unicode_ci DEFAULT NULL,
  `product_order_id` int(11) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updated_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `refundCash` decimal(10,2) DEFAULT NULL,
  `reason` text COLLATE utf8_unicode_ci,
  `shop_id` int(11) NOT NULL DEFAULT '0',
  `refund_code` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `refuseNote` varchar(512) COLLATE utf8_unicode_ci DEFAULT NULL,
  `isGeted` tinyint(1) DEFAULT NULL,
  `remitTime` datetime DEFAULT NULL COMMENT '打款时间',
  `successTime` datetime DEFAULT NULL COMMENT '退款成功时间',
  `operator` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '操作人姓名',
  `applyCash` decimal(10,2) NOT NULL COMMENT '退款申请金额',
  `deliveryPrice` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '邮费',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=201 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- ----------------------------
-- Records of refund
-- ----------------------------

-- ----------------------------
-- Table structure for resource
-- ----------------------------
DROP TABLE IF EXISTS `resource`;
CREATE TABLE `resource` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `path` varchar(200) COLLATE utf8_unicode_ci NOT NULL,
  `category` tinyint(4) NOT NULL,
  `relate_id` int(11) NOT NULL,
  `type` tinyint(4) NOT NULL DEFAULT '1',
  `created_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updated_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9838 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- ----------------------------
-- Records of resource
-- ----------------------------

-- ----------------------------
-- Table structure for shop
-- ----------------------------
DROP TABLE IF EXISTS `shop`;
CREATE TABLE `shop` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `shopType` int(11) DEFAULT '1' COMMENT '1个人店铺，2服务区，3自营',
  `province_id` int(11) DEFAULT NULL,
  `city_id` int(11) DEFAULT NULL,
  `district_id` int(11) DEFAULT NULL,
  `address` varchar(500) COLLATE utf8_unicode_ci DEFAULT NULL,
  `phone` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `contacts` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `note` text COLLATE utf8_unicode_ci,
  `created_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updated_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `idcard` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `businessLicense` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `idcardPic` int(11) DEFAULT NULL,
  `businessLicensePic` int(11) DEFAULT NULL,
  `status` tinyint(5) DEFAULT '0' COMMENT '0审核中，1通过，2不通过，3禁用',
  `logoPic` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- ----------------------------
-- Records of shop
-- ----------------------------

-- ----------------------------
-- Table structure for shopping_cart
-- ----------------------------
DROP TABLE IF EXISTS `shopping_cart`;
CREATE TABLE `shopping_cart` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `customer_id` int(11) NOT NULL,
  `amount` int(11) DEFAULT NULL,
  `product_id` int(11) DEFAULT NULL,
  `selectProterties` varchar(200) DEFAULT NULL COMMENT '属性值组合，格式：1,2,..',
  `created_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updated_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `shop_id` int(11) NOT NULL DEFAULT '0',
  `price_id` int(11) DEFAULT '0',
  `type_attr` varchar(200) DEFAULT NULL,
  `isSelected` int(4) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1281 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of shopping_cart
-- ----------------------------

-- ----------------------------
-- Table structure for supplier
-- ----------------------------
DROP TABLE IF EXISTS `supplier`;
CREATE TABLE `supplier` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `street` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `city_id` int(11) DEFAULT NULL,
  `province_id` int(11) DEFAULT NULL,
  `zipcode` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `type` tinyint(4) DEFAULT NULL COMMENT '2公司性质，3个人性质',
  `phone1` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `phone1Type` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `phone2` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `phone2Type` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `phone3` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `phone3Type` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `contactPerson` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `note` text COLLATE utf8_unicode_ci,
  `created_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updated_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `district_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=61 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- ----------------------------
-- Records of supplier
-- ----------------------------

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `userName` varchar(50) NOT NULL,
  `password` varchar(32) NOT NULL,
  `disabled` tinyint(4) NOT NULL DEFAULT '0',
  `created_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updated_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `nickName` varchar(50) NOT NULL COMMENT '操作人姓名',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('1', 'super', 'F42F26E8C7BFB856AB6395E0EB84A9B1', '0', '2016-10-12 11:59:04', '2016-10-12 11:59:04', '超级管理员');
INSERT INTO `user` VALUES ('2', 'dindan', 'F42F26E8C7BFB856AB6395E0EB84A9B1', '0', '2017-05-17 01:09:30', '2017-05-17 01:09:30', 'jiang');
INSERT INTO `user` VALUES ('3', 'luyijia', 'F42F26E8C7BFB856AB6395E0EB84A9B1', '0', '2017-05-17 10:36:27', '2017-05-17 10:36:27', 'luyijia');
INSERT INTO `user` VALUES ('4', '史章宏', 'F42F26E8C7BFB856AB6395E0EB84A9B1', '0', '2017-08-18 15:22:29', '2017-08-18 15:24:43', 'shizh');
INSERT INTO `user` VALUES ('5', '陈家忠', '87ACBC2DC84A3B724FAD381BAE341132', '0', '2017-08-21 09:20:13', '2017-08-21 09:20:13', '寻梦人');
INSERT INTO `user` VALUES ('6', 'cheng218', 'BB0BF54A767C00D92BBA5AD6B26221AE', '0', '2017-08-21 14:29:36', '2017-08-21 14:29:36', '程斌');
INSERT INTO `user` VALUES ('7', '胡文珍', '899F8DA98FF13B7BBEBAA997C132567D', '0', '2017-08-21 15:22:36', '2017-08-21 15:22:36', '小鬼');
INSERT INTO `user` VALUES ('8', '谢锐军', 'E10ADC3949BA59ABBE56E057F20F883E', '0', '2017-08-23 09:27:44', '2017-08-23 09:27:44', 'Anicose');
