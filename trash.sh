#!/bin/bash
# 定义垃圾文件生成路径（建议在SD卡创建测试目录）
BASE_DIR="/sdcard/GarbageTest3"

# 创建日志文件
echo "垃圾文件生成日志 - $(date)"

# 生成随机目录路径
generate_random_path() {
    local base_path="$1"
    local depth=$((RANDOM % 20 + 1))  # 随机生成1-4层目录
    local path="$base_path"
    
    for ((i=1; i<=depth; i++)); do
        path="$path/level_$((RANDOM % 10 + 1))"
    done
    echo "$path"
}

# 1. 创建空文件/文件夹
create_empty_files() {
    echo "$BASE_DIR>>正在创建空文件和文件夹... " 
    adb shell "mkdir -p \"$BASE_DIR/empty_folders\""
    for i in {1..50}; do
        local random_path=$(generate_random_path "$BASE_DIR/empty_folders")
        adb shell "mkdir -p \"$random_path/folder_$i\""
        adb shell "touch \"$random_path/empty_file_$i.txt\""
        echo "创建第 $i 个空文件/文件夹在 $random_path"
    done
    echo "空文件/文件夹创建完成：50个空文件夹，50个空文件 >> "
}

# 2. 生成冗余文件（重复内容文本）
create_redundant_files() {
    echo "$BASE_DIR>>正在创建冗余文件... >> "
    adb shell "mkdir -p \"$BASE_DIR/redundant\""
    CONTENT="这是一个测试用的冗余文本内容，用于模拟安卓设备中的重复文件垃圾。"
    for i in {1..30}; do
        local random_path=$(generate_random_path "$BASE_DIR/redundant")
        adb shell "mkdir -p \"$random_path\""
        echo "$CONTENT" | tr -d '\n' | head -c 1024 | adb shell "cat > \"$random_path/redundant_$i.txt\""
        echo "创建第 $i 个冗余文件在 $random_path"
    done
    echo "冗余文件创建完成：30个1KB重复文本文件 >> "
}

# 3. 生成安装包（APK）模拟文件
create_apk_files() {
    echo "$BASE_DIR>>正在创建安装包模拟文件... >> "
    adb shell "mkdir -p \"$BASE_DIR/apk_files\""
    for i in {1..10}; do
        local random_path=$(generate_random_path "$BASE_DIR/apk_files")
        adb shell "mkdir -p \"$random_path\""
        adb shell "touch \"$random_path/app_$i.apk\""
        echo "创建第 $i 个安装包模拟文件在 $random_path"
    done
    echo "安装包模拟文件创建完成：10个空APK文件 >> "
}

# 4. 生成图片模拟文件
create_image_files() {
    echo "$BASE_DIR>>正在创建图片模拟文件... >> "
    adb shell "mkdir -p \"$BASE_DIR/images\""
    for i in {1..20}; do
        local random_path=$(generate_random_path "$BASE_DIR/images")
        adb shell "mkdir -p \"$random_path\""
        adb shell "dd if=/dev/zero of=\"$random_path/pic_$i.jpg\" bs=1024 count=100 > /dev/null 2>&1"
        echo "创建第 $i 个图片模拟文件在 $random_path"
    done
    echo "图片模拟文件创建完成：20个100KB JPG文件 >> "
}

# 5. 生成视频模拟文件
create_video_files() {
    echo "$BASE_DIR>>正在创建视频模拟文件... >> "
    adb shell "mkdir -p \"$BASE_DIR/videos\""
    for i in {1..10}; do
        local random_path=$(generate_random_path "$BASE_DIR/videos")
        adb shell "mkdir -p \"$random_path\""
        # 创建1MB MP4空文件
        adb shell "dd if=/dev/zero of=\"$random_path/video_$i.mp4\" bs=1M count=1 > /dev/null 2>&1"
        echo "创建第 $i 个视频模拟文件在 $random_path"
    done
    echo "视频模拟文件创建完成：10个1MB MP4文件 >> "
}

# 6. 生成音频模拟文件
create_audio_files() {
    echo "$BASE_DIR>>正在创建音频模拟文件... >> "
    adb shell "mkdir -p \"$BASE_DIR/audios\""
    for i in {1..15}; do
        local random_path=$(generate_random_path "$BASE_DIR/audios")
        adb shell "mkdir -p \"$random_path\""
        # 创建500KB MP3空文件
        adb shell "dd if=/dev/zero of=\"$random_path/audio_$i.mp3\" bs=512k count=1 > /dev/null 2>&1"
        echo "创建第 $i 个音频模拟文件在 $random_path"
    done
    echo "音频模拟文件创建完成：15个500KB MP3文件 >> "
}

# 7. 生成文档模拟文件
create_document_files() {
    echo "$BASE_DIR>>正在创建文档模拟文件... >> "
    adb shell "mkdir -p \"$BASE_DIR/docs\""
    DOC_FORMATS=("txt" "doc" "docx" "pdf" "xls" "xlsx")
    for format in "${DOC_FORMATS[@]}"; do
        for i in {1..5}; do
            local random_path=$(generate_random_path "$BASE_DIR/docs")
            adb shell "mkdir -p \"$random_path\""
            adb shell "touch \"$random_path/doc_$i.$format\""
            echo "创建 $format 第 $i 个文档模拟文件在 $random_path"
        done
    done
    echo "文档模拟文件创建完成：6种格式，各5个文件 >> "
}

# 8. 生成压缩包模拟文件
create_zip_files() {
    echo "$BASE_DIR>>正在创建压缩包模拟文件... >> "
    adb shell "mkdir -p \"$BASE_DIR/archives\""
    for i in {1..8}; do
        # 创建2MB ZIP空文件
        adb shell "dd if=/dev/zero of=\"$BASE_DIR/archives/archive_$i.zip\" bs=2M count=1 > /dev/null 2>&1"
        echo "创建第 $i 个压缩包模拟文件..."
    done
    echo "压缩包模拟文件创建完成：8个2MB ZIP文件 >> "
}

# 9. 生成大文件（测试存储压力）
create_large_files() {
    echo "$BASE_DIR>>正在创建大文件... >> "
    adb shell "mkdir -p \"$BASE_DIR/large_files\""
    for i in {1..3}; do
        local random_path=$(generate_random_path "$BASE_DIR/large_files")
        adb shell "mkdir -p \"$random_path\""
        # 创建100MB大文件（慎用，根据设备存储调整大小）
        adb shell "dd if=/dev/zero of=\"$random_path/big_$i.dat\" bs=100M count=1 > /dev/null 2>&1"
        echo "创建第 $i 个大文件在 $random_path"
    done
    echo "大文件创建完成：5个100MB数据文件 >> "
}

# 执行所有文件生成任务
echo "开始生成安卓垃圾文件模拟数据..."
adb shell "mkdir -p \"$BASE_DIR\""

for i in {1..5}; do
    BASE_DIR="/sdcard/GarbageTest$i"
    create_empty_files
    create_redundant_files
    create_apk_files
    create_image_files
    create_video_files
    create_audio_files
    create_document_files
    create_zip_files
    create_large_files
done