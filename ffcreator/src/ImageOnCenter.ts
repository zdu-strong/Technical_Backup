import { FFImage } from 'ffcreator';
import ImageSize from 'image-size';
import * as mathjs from 'mathjs'

// The picture is placed in the middle of the scene, centered up and down, centered left and right
export async function imageOnCenter(config: {
  width: number;
  height: number;
  imagePath: string;
}) {
  const imageInfo = ImageSize(config.imagePath);
  const imageWidth =
    mathjs.divide(config.width, config.height) > mathjs.divide(imageInfo.width, imageInfo.height)
      ? Math.floor(mathjs.multiply(config.height, mathjs.divide(imageInfo.width, imageInfo.height)))
      : config.width;
  const imageHeight =
    mathjs.divide(config.width, config.height) > mathjs.divide(imageInfo.width, imageInfo.height)
      ? config.height
      : Math.floor(mathjs.multiply(config.width, mathjs.divide(imageInfo.height, imageInfo.width)));
  const ffimage = new FFImage({
    path: config.imagePath,
    resetXY: true,
    x: Math.floor(mathjs.divide(mathjs.subtract(config.width, imageWidth), 2)),
    y: Math.floor(mathjs.divide(mathjs.subtract(config.height, imageHeight), 2)),
    width: imageWidth,
    height: imageHeight,
  });
  return ffimage;
}