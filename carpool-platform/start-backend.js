const { exec } = require('child_process');
const fs = require('fs');

console.log('🚀 启动长途拼车平台后端...');

// 检查是否已安装Java
exec('which java', (error) => {
  if (error) {
    console.log('❌ Java未安装，无法启动后端服务');
    console.log('请先安装Java 17+：');
    console.log('Ubuntu/Debian: sudo apt install openjdk-17-jdk');
    console.log('CentOS/RHEL: sudo yum install java-17-openjdk');
    process.exit(1);
  }
  
  console.log('✅ Java已安装，开始启动后端...');
  
  // 启动后端
  const backendProcess = exec('cd backend && mvn spring-boot:run', (error, stdout, stderr) => {
    if (error) {
      console.error(`后端启动失败: ${error}`);
      return;
    }
    console.log(stdout);
  });
  
  backendProcess.stdout.on('data', (data) => {
    console.log(`后端: ${data}`);
  });
  
  backendProcess.stderr.on('data', (data) => {
    console.error(`后端错误: ${data}`);
  });
});
