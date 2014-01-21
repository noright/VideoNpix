VideoNpix
=========
  最新为VideoNpix_v3.0.0
  所需文件目录组织如下：
    videonpix
    |-- barcode
    |   |-- LANDSCAPE
    |   |   |-- 9787111267768.jpg
    |   |   |-- ...
    |   |   `-- 9789866072000.jpg
    |   |-- noinformation.jpg
    |   |-- PORTRAIT
    |   |   |-- 9787111267768.jpg
    |   |   |-- ...
    |   |   `-- 9789866072000.jpg
    |   `-- qbackground.jpg
    |-- log
    |   `-- log_2014_01_21.txt
    |-- thumbnail
    |   |-- 10.jpg
    |   |-- ...
    |   `-- qw.jpg
    `-- video
        |-- 10.mp4
        |-- ...
        `-- qw.mp4
  有主目录videonpix，下面分别建立4个子目录barcode，log，thumbnail，video。
    barcode中LANDSCAPE和PORTRAIT目录对应barcode扫描后barcode显示的横屏和竖屏的图片
    log中会自动生成videonpix运行时的log
    thumbnail中放置图片文件，文件多少决定videonpix显示pix数目，文件名称应与视频名称对应（如：1.jpg对应1.mp4）
    video中存放视频文件文件名应与图片文件名称对应
    ps：如果图片文件多于视频文件，也会显示多出图片，但点击图片时并无对应视频，会播放下一图片对应的视频。
