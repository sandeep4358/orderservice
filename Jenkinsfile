pipeline{
    agent any
    stages{
        stage('checkout'){
            steps{
                echo 'checkout!!!!!'
                sh 'mvn package'
            }
        }
        stage('Build'){
                    steps{
                        echo 'Build!!!!!'
                        deploy adapters: [tomcat9(credentialsId: 'd9f55980-a94e-4f7d-96f3-d3ec495ac706',
                        path: '',
                        url: 'http://192.168.0.102:8082/')],
                        contextPath: '/order-service',
                        war: '**/order-service'
                    }
                }
    }
}