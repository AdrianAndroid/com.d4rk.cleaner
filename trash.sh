#!/bin/bash
# 定义垃圾文件生成路径（建议在SD卡创建测试目录）
BASE_DIR="/sdcard/GarbageTest3"

# 创建日志文件
echo "垃圾文件生成日志 - $(date)"

# 生成随机目录路径
generate_random_path() {
    local base_path="$1"
    local depth=$((RANDOM % 10 + 1))  # 随机生成1-4层目录
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
    for i in {1..20}; do
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
    
    # 定义文件扩展名数组
    local extensions=(
        "DS_Store"
        "Spotlight-V100"
        "fseventsd"
        "log"
        "logs"
        "old"
        "tmp"
        "temp"
        "splashad"
        "db"
        "nomedia"
    )
    
    # 创建不同类型的冗余文件
    for i in {1..20}; do
        local random_path=$(generate_random_path "$BASE_DIR/redundant")
        adb shell "mkdir -p \"$random_path\""
        
        # 随机选择一个扩展名
        local ext_index=$((RANDOM % ${#extensions[@]}))
        local extension=${extensions[$ext_index]}
        
        # 随机生成文件大小（1KB到10MB）
        local size_kb=$((RANDOM % 1024 + 1))
        
        # 根据扩展名生成不同的内容
        local file_content=""
        case "$extension" in
            "DS_Store"|"Spotlight-V100"|"fseventsd")
                file_content="系统缓存文件内容"
                ;;
            "log"|"logs")
                file_content="日志记录：$CONTENT"
                ;;
            "old"|"tmp"|"temp")
                file_content="临时文件：$CONTENT"
                ;;
            "splashad")
                file_content="广告缓存：$CONTENT"
                ;;
            "db")
                file_content="数据库文件：$CONTENT"
                ;;
            "nomedia")
                file_content="媒体文件标记"
                ;;
        esac
        
        # 重复内容到指定大小
        local repeated_content=""
        for ((j=0; j<size_kb; j++)); do
            repeated_content="${repeated_content}${file_content}"
        done
        
        echo "$repeated_content" | adb shell "cat > \"$random_path/redundant_${i}.${extension}\""
        echo "创建第 $i 个冗余文件在 $random_path，类型：${extension}，大小：${size_kb}KB"
    done
    echo "冗余文件创建完成：30个不同类型的垃圾文件 >> "
}

# 3. 生成安装包（APK）模拟文件
create_apk_files() {
    echo "$BASE_DIR>>正在创建安装包模拟文件... >> "
    adb shell "mkdir -p \"$BASE_DIR/apk_files\""
    
    # 定义一些常见的应用名称
    local app_names=(
        "社交聊天" "视频播放器" "音乐播放器" "图片编辑" 
        "文件管理" "系统清理" "游戏" "浏览器" 
        "天气预报" "日历备忘录"
    )
    
    for i in {1..10}; do
        local random_path=$(generate_random_path "$BASE_DIR/apk_files")
        adb shell "mkdir -p \"$random_path\""
        
        # 随机选择一个应用名称
        local name_index=$((RANDOM % ${#app_names[@]}))
        local app_name=${app_names[$name_index]}
        
        # 生成随机版本号
        local version="$((RANDOM % 10 + 1)).$((RANDOM % 10)).$((RANDOM % 10))"
        
        # 生成随机大小（20MB到200MB）
        local size_mb=$((RANDOM % 180 + 20))
        local size_bytes=$((size_mb * 1024 * 1024))
        
        # 创建APK文件内容（包含基本的APK文件结构信息）
        local manifest="<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
        manifest+="<manifest package=\"com.example.${app_name}\" version=\"${version}\">\n"
        manifest+="<application android:label=\"${app_name}\"/>\n"
        manifest+="</manifest>"
        
        # 使用dd命令创建指定大小的文件
        echo "$manifest" | adb shell "cat > \"$random_path/app_${app_name}_v${version}.apk\""
        adb shell "dd if=/dev/zero bs=1M count=$size_mb >> \"$random_path/app_${app_name}_v${version}.apk\" 2>/dev/null"
        
        echo "创建第 $i 个安装包文件在 $random_path，应用：${app_name}，版本：${version}，大小：${size_mb}MB"
    done
    echo "安装包文件创建完成：10个模拟APK文件 >> "
}

create_image_files() {
    echo "$BASE_DIR>>正在创建图片模拟文件... >> "
    adb shell "mkdir -p \"$BASE_DIR/images\""
    
    # 定义图片扩展名数组
    local extensions=(
        "jpg"
        "jpeg"
        "webp"
        "bmp"
        "png"
        "raw"
        "psd"
    )
    
    # 定义一些常见的图片名称前缀
    local prefixes=(
        "IMG" "PHOTO" "SCREENSHOT" "CAMERA" 
        "PIC" "IMAGE" "WALLPAPER" "DCIM"
    )
    
    for i in {1..20}; do
        local random_path=$(generate_random_path "$BASE_DIR/images")
        adb shell "mkdir -p \"$random_path\""
        
        # 随机选择文件扩展名和前缀
        local ext_index=$((RANDOM % ${#extensions[@]}))
        local prefix_index=$((RANDOM % ${#prefixes[@]}))
        local extension=${extensions[$ext_index]}
        local prefix=${prefixes[$prefix_index]}
        
        # 生成随机大小（1MB到20MB）
        local size_mb=$((RANDOM % 19 + 1))
        local size_bytes=$((size_mb * 1024 * 1024))
        
        # 生成随机日期时间戳（最近一年内）
        local days_ago=$((RANDOM % 365))
        local timestamp=$(date -v-${days_ago}d +%Y%m%d_%H%M%S)
        
        # 创建图片文件并填充随机内容
        local filename="${prefix}_${timestamp}.${extension}"
        adb shell "dd if=/dev/urandom of=\"$random_path/$filename\" bs=1M count=$size_mb 2>/dev/null"
        
        echo "创建第 $i 个图片文件在 $random_path，类型：${extension}，大小：${size_mb}MB"
    done
    echo "图片文件创建完成：20个不同类型的图片文件 >> "
}

# 5. 生成视频模拟文件
create_video_files() {
    echo "$BASE_DIR>>正在创建视频模拟文件... >> "
    adb shell "mkdir -p \"$BASE_DIR/videos\""
    
    # 定义视频扩展名数组
    local extensions=(
        "mp4" "avi" "mov" "mkv" "flv" "webm" "vob" "ogv"
        "gif" "gifv" "mng" "wmv" "yuv" "amv"
    )
    
    # 定义视频来源前缀
    local prefixes=(
        "VID" "VIDEO" "MOVIE" "SCREEN_RECORDING" 
        "CAMERA" "CLIP" "REC" "VLOG"
    )
    
    for i in {1..10}; do
        local random_path=$(generate_random_path "$BASE_DIR/videos")
        adb shell "mkdir -p \"$random_path\""
        
        # 随机选择文件扩展名和前缀
        local ext_index=$((RANDOM % ${#extensions[@]}))
        local prefix_index=$((RANDOM % ${#prefixes[@]}))
        local extension=${extensions[$ext_index]}
        local prefix=${prefixes[$prefix_index]}
        
        # 生成随机大小（50MB到500MB）
        local size_mb=$((RANDOM % 450 + 50))
        
        # 生成随机日期时间戳（最近一年内）
        local days_ago=$((RANDOM % 365))
        local timestamp=$(date -v-${days_ago}d +%Y%m%d_%H%M%S)
        
        # 创建视频文件并填充随机内容
        local filename="${prefix}_${timestamp}.${extension}"
        adb shell "dd if=/dev/urandom of=\"$random_path/$filename\" bs=1M count=$size_mb 2>/dev/null"
        
        # 添加一些视频文件的基本信息到文件头部
        local mins=$(expr $RANDOM % 10)
        local secs=$(expr $RANDOM % 60)
        local header="VIDEO_INFO={\"duration\":\"00:$mins:$secs\",\"resolution\":\"1920x1080\",\"fps\":\"30\",\"codec\":\"h264\"}"
        echo "$header" | adb shell "dd of=\"$random_path/$filename\" conv=notrunc 2>/dev/null"
        
        echo "创建第 $i 个视频文件在 $random_path，类型：${extension}，大小：${size_mb}MB"
    done
    echo "视频文件创建完成：10个不同类型的视频文件 >> "
}

# 6. 生成音频模拟文件
create_audio_files() {
    echo "$BASE_DIR>>正在创建音频模拟文件... >> "
    adb shell "mkdir -p \"$BASE_DIR/audios\""
    
    # 定义音频扩展名数组
    local extensions=(
        "mp3" "m4a" "wav" "flac" "midi" 
        "wma" "aac" "ogg" "opus" "aiff"
    )
    
    # 定义音频来源前缀
    local prefixes=(
        "AUDIO" "VOICE" "RECORDING" "MUSIC" 
        "SONG" "PODCAST" "MEMO" "NOTE"
    )
    
    for i in {1..15}; do
        local random_path=$(generate_random_path "$BASE_DIR/audios")
        adb shell "mkdir -p \"$random_path\""
        
        # 随机选择文件扩展名和前缀
        local ext_index=$((RANDOM % ${#extensions[@]}))
        local prefix_index=$((RANDOM % ${#prefixes[@]}))
        local extension=${extensions[$ext_index]}
        local prefix=${prefixes[$prefix_index]}
        
        # 生成随机大小（2MB到50MB）
        local size_mb=$((RANDOM % 48 + 2))
        
        # 生成随机日期时间戳（最近一年内）
        local days_ago=$((RANDOM % 365))
        local timestamp=$(date -v-${days_ago}d +%Y%m%d_%H%M%S)
        
        # 创建音频文件并填充随机内容
        local filename="${prefix}_${timestamp}.${extension}"
        adb shell "dd if=/dev/urandom of=\"$random_path/$filename\" bs=1M count=$size_mb 2>/dev/null"
        
        # 添加一些音频文件的基本信息到文件头部
        local mins=$(expr $RANDOM % 10)
        local secs=$(expr $RANDOM % 60)
        local header="AUDIO_INFO={\"duration\":\"00:$mins:$secs\",\"bitrate\":\"320kbps\",\"sample_rate\":\"44100Hz\",\"channels\":\"2\"}"
        echo "$header" | adb shell "dd of=\"$random_path/$filename\" conv=notrunc 2>/dev/null"
        
        echo "创建第 $i 个音频文件在 $random_path，类型：${extension}，大小：${size_mb}MB"
    done
    echo "音频文件创建完成：15个不同类型的音频文件 >> "
}

# 7. 生成文档模拟文件
create_document_files() {
    echo "$BASE_DIR>>正在创建文档模拟文件... >> "
    adb shell "mkdir -p \"$BASE_DIR/docs\""
    
    # 定义文档扩展名数组
    local DOC_FORMATS=(
        # Word相关格式
        "doc" "dot" "wbk" "docx" "docm" "dotx" "dotm" "docb"
        # Excel相关格式
        "xls" "xlt" "xlm" "xlsx" "xlsm" "xltx" "xltm" "xlsb" "xla" "xlam" "xll" "xlw"
        # PowerPoint相关格式
        "ppt" "pot" "pps" "ppa" "pptx" "pptm" "potx" "potm" "ppam" "ppsx" "ppsm" "sldx" "sldm"
        # Access相关格式
        "accda" "accdb" "accde" "accdr" "accdt" "accdu"
        # 其他Office格式
        "one" "ecf" "pub" "pdf" "txt"
    )
    
    # 定义文档类型和对应的内容模板
#    declare -A CONTENT_TEMPLATES=(
#        ["doc"]="这是一个Word文档的示例内容，包含了一些文字段落和格式。"
#        ["xls"]="A1单元格:数据1\nB1单元格:数据2\nC1单元格:数据3"
#        ["ppt"]="第1页:演示文稿标题\n第2页:内容概述\n第3页:详细信息"
#        ["pdf"]="这是一个PDF文档的示例内容，包含了一些文字和格式信息。"
#        ["txt"]="这是一个文本文件的示例内容。\n包含多行文本和一些基本格式。"
#    )
    
    # 定义文档前缀
    local prefixes=(
        "报告" "方案" "总结" "计划" "备忘录"
        "数据" "表格" "演示" "文档" "记录"
    )
    
    for format in "${DOC_FORMATS[@]}"; do
        for i in {1..3}; do
            local random_path=$(generate_random_path "$BASE_DIR/docs")
            adb shell "mkdir -p \"$random_path\""
            
            # 随机选择文档前缀
            local prefix_index=$((RANDOM % ${#prefixes[@]}))
            local prefix=${prefixes[$prefix_index]}
            
            # 生成随机日期
            local timestamp=$(date -v-$((RANDOM % 365))d +%Y%m%d)
            
            # 生成随机大小（100KB到10MB）
            local size_kb=$((RANDOM % 9900 + 100))
            
            # 创建基本文件
            local filename="${prefix}_${timestamp}_v$i.$format"
            
            # 根据文件类型选择内容模板
            local content_type=${format%[0-9]*}
#            local content=${CONTENT_TEMPLATES[$content_type]:-"这是一个${format}格式的文档示例。"}
            local content="这是一个${format}格式的文档示例。"

            # 创建文件并写入内容
            echo "$content" | adb shell "cat > \"$random_path/$filename\""
            
            # 填充到目标大小
            adb shell "dd if=/dev/urandom bs=1024 count=$size_kb >> \"$random_path/$filename\" 2>/dev/null"
            
            echo "创建 $format 格式第 $i 个文档文件在 $random_path，大小：${size_kb}KB"
        done
    done
    echo "文档文件创建完成：${#DOC_FORMATS[@]}种格式，各3个文件 >> "
}

# 8. 生成压缩包模拟文件
create_zip_files() {
    echo "$BASE_DIR>>正在创建压缩包模拟文件... >> "
    adb shell "mkdir -p \"$BASE_DIR/archives\""
    
    # 定义压缩文件扩展名数组
    local archive_extensions=("7z" "ace" "arj" "bz" "bz2" "cab" "cb7" "cbt" "cbz" "cbr" "dgc" "dmg" "ear" 
        "gz" "gzip" "ha" "ice" "jar" "kgb" "lzh" "lz" "lzma" "lzo" "pak" "partimg" "pea" "pet" "pk3" 
        "pk4" "rar" "rpm" "rz" "s7z" "sda" "sea" "sen" "sfark" "sfx" "shar" "sit" "sitx" "sqx" "tar" 
        "tar.bz2" "tar.gz" "tar.xz" "tgz" "tlz" "txz" "udf" "utz" "uu" "uue" "war" "wim" "xar" "xp3" 
        "xz" "z" "zip" "zipx" "zoo" "zpaq")
    
    # 为每种扩展名创建2个文件
    for ext in "${archive_extensions[@]}"; do
        for i in {1..2}; do
            # 生成随机大小（5MB到50MB）
            local size=$((RANDOM % 46 + 5))
            local timestamp=$(date +%Y%m%d_%H%M%S)
            local filename="archive_${timestamp}_${i}.${ext}"
            
            # 创建非空文件，使用/dev/urandom生成随机内容
            adb shell "dd if=/dev/urandom of=\"$BASE_DIR/archives/$filename\" bs=1M count=$size > /dev/null 2>&1"
            
            # 在文件开头添加基本的压缩文件头部信息
            adb shell "printf 'Archive Type: %s\nCreated: %s\nSize: %dMB\nCompression: Random\nEncryption: None\n' '$ext' '$(date)' '$size' | dd conv=notrunc of=\"$BASE_DIR/archives/$filename\" > /dev/null 2>&1"
            
            echo "创建压缩包文件：$filename (${size}MB)"
        done
    done
    
    echo "压缩包模拟文件创建完成：共创建${#archive_extensions[@]}种格式，每种2个文件 >> "
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

for i in {1..3}; do
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