README
--------------------------------------------
Name: Idan Elazar

How to run the application locally:

•You need to have java 17 on yout device
•you need to have Maven installed on your system .

Navigate to Project Directory: Open a command prompt or terminal and navigate to the directory of your Spring Boot project.

Build the Project: Run the following Maven command to build your project:
mvn clean package

Run the Application: Once the build is successful, navigate to the directory containing the JAR file generated in the target folder. Then, run the following command to start the application:

java -jar money-transfer-service-0.0.1-SNAPSHOT.jar

to shutdown the app use ctrl+c.


------------------------------------------------
AWS
--------------------------------
 I used 2 AWS services for the project RDS and Elastic Beanstalk

RDS
--------------------------------
seting up the database (in my case mysql) remotely enjoying the database service Amazon offer such as: scalabilty, automated backups etc.

Elastic Beanstalk
--------------------------------
Deploying the application at remote server make it accessible for all.
Elastic Beanstalk is an abstraction over EC2 instances.
In order to make it work, I needed to also create a role and grant premmision policies for:
AWSElasticBeanstalkWebTier, AWSElasticBeanstalkWorkerTier and AWSElasticBeanstalkMulticontainerDocker.

--------------------------------------------------
Assumption I did in this project:

1) Sender can sender money regardless for it's balance(up to 1000$).
2) Sender can not check their balance.
3) Recipients can not send money.
4)I did not gave the option for recipients to withdraw morw money then they have.