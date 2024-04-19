use jni::objects::*;
use jni::JNIEnv;
use jni::sys::jlong;
use sysinfo::Pid;

#[no_mangle]
pub extern "C" fn Java_org_dindier_oicraft_util_CodeChecker_getProcessMemoryUsage(_env: JNIEnv, _class: JClass, pid: jlong) -> jlong {
    get_process_memory_usage(pid as usize)
}

pub fn get_process_memory_usage(pid: usize) -> jlong {
    let system = sysinfo::System::new_all();
    let pid = Pid::from(pid);

    if let Some(process) = system.process(pid) {
        process.memory() as jlong
    } else {
        -1
    }
}


#[cfg(test)]
mod tests {
    use std::process::Command;
    use std::thread::sleep;
    use std::time::Duration;
    use super::*;

    #[test]
    fn test_jni() {
        let mut process = Command::new("E:\\C++\\temp\\Release\\demo.exe")
            .spawn()
            .expect("failed to execute process");
        let pid = process.id();
        while process.try_wait().unwrap().is_none() {
            let usage = get_process_memory_usage(pid as usize);
            println!("Memory usage: {} KB", usage / 1024);
            sleep(Duration::from_millis(500));
        }
    }
}
