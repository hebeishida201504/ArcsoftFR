

SET FOREIGN_KEY_CHECKS=0;


CREATE TABLE `admin` (
  `Id` int(20) NOT NULL,
  `Name` varchar(20) DEFAULT NULL,
  `Password` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;




