import path from 'path';
import { FFCreator, FFScene } from 'ffcreator';
import { imageOnCenter } from '@/ImageOnCenter';
import * as mathjs from 'mathjs'

export async function generateVideo(config: {
  cacheDir: string;
  outputVideoFilePath: string;
}) {
  // pre-defined variables
  const audioOneFilePathOfAreYouOk = path.join(
    __dirname,
    'audio',
    'Are You OK.mp3',
  );
  const paddyOne = path.join(__dirname, 'image', 'paddyOne.png');
  const paddyTwo = path.join(__dirname, 'image', 'paddyTwo.jpg');
  const paddyThree = path.join(__dirname, 'image', 'paddyThree.png');
  const paddyFour = path.join(__dirname, 'image', 'paddyFour.png');
  const girlImage = path.join(__dirname, "image", "girl.jpg");
  const width = 800;
  const height = 550;
  // Basic configuration of video
  const ffcreator = new FFCreator({
    cacheDir: config.cacheDir,
    output: config.outputVideoFilePath,
    width: width,
    height: height,
    cover: girlImage,
    audioLoop: false,
    fps: 120,
    debug: false,
    log: true,
    clarity: "high",
    preset: "veryslow",
  });
  {
    // Background music
    ffcreator.addAudio({
      path: audioOneFilePathOfAreYouOk,
      start: 0,
      volume: 1,
      bg: true,
      loop: true,
    });
  }
  {
    // Scene 1, black background
    const ffscene = new FFScene();
    ffscene.setBgColor('#000000');
    ffscene.setDuration(3);
    ffscene.setTransition('Colorful', 3);
    ffscene.addChild(
      await imageOnCenter({
        width,
        height,
        imagePath: girlImage,
      }),
    );
    ffcreator.addChild(ffscene);
  }
  {
    // Scene 2, the first picture
    const ffscene = new FFScene();
    ffscene.setBgColor('#000000');
    ffscene.setDuration(5);
    ffscene.setTransition('MoveLeft', 1);
    ffscene.addChild(
      await imageOnCenter({
        width,
        height,
        imagePath: paddyOne,
      }),
    );
    ffcreator.addChild(ffscene);
  }
  {
    // Scene 3, the second picture
    const ffscene = new FFScene();

    ffscene.setBgColor('#000000');
    ffscene.setDuration(9);
    ffscene.setTransition('InvertedPageCurl', 3);
    const ffimage = await imageOnCenter({
      width,
      height,
      imagePath: paddyTwo,
    });

    ffimage.addEffect('fadeIn', 0, 0);
    ffimage.addAnimate({
      from: { scale: 1, alpha: 1 },
      to: {
        x: Math.floor(mathjs.divide(width, 2)),
        y: Math.floor(mathjs.divide(height, 2)),
        scale: 0.1,
        alpha: 0.2,
      },
      time: 3,
      delay: 1,
      ease: "Bounce.In",
    });
    ffimage.addAnimate({
      from: {
        x: Math.floor(mathjs.divide(width, 2)),
        y: Math.floor(mathjs.divide(height, 2)),
        scale: 0.1,
        alpha: 0.2,
      },
      to: { scale: 1, alpha: 1 },
      time: 3,
      delay: 4,
      ease: "Bounce.Out",
    });
    ffscene.addChild(ffimage);
    ffcreator.addChild(ffscene);
  }
  {
    // Scene 4, the third picture
    const ffscene = new FFScene();

    ffscene.setBgColor('#000000');
    ffscene.setDuration(5);
    ffscene.setTransition('Radial', 1);
    ffscene.addChild(
      await imageOnCenter({
        width,
        height,
        imagePath: paddyThree,
      }),
    );
    ffcreator.addChild(ffscene);
  }
  {
    // Scene 5, the fourth picture
    const ffscene = new FFScene();
    ffscene.setBgColor('#000000');
    ffscene.setDuration(2);
    ffscene.addChild(
      await imageOnCenter({
        width,
        height,
        imagePath: paddyFour,
      }),
    );
    ffcreator.addChild(ffscene);
  }
  return ffcreator;
}