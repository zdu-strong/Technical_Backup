import * as BABYLON from '@babylonjs/core';

async function createScene(engine: BABYLON.Engine, canvasRef: React.RefObject<HTMLCanvasElement>) {

  const scene = new BABYLON.Scene(engine);
  // Create a FreeCamera, and set its position to {x: 0, y: 5, z: -10}
  const camera = new BABYLON.FreeCamera('camera1', new BABYLON.Vector3(0, 5, -10), scene);
  // Target the camera to scene origin
  camera.setTarget(BABYLON.Vector3.Zero());
  // Attach the camera to the canvas
  camera.attachControl(canvasRef.current, false);
  // Create a basic light, aiming 0, 1, 0 - meaning, to the sky
  new BABYLON.HemisphericLight('light1', new BABYLON.Vector3(0, 1, 0), scene);
  // Create a built-in "sphere" shape using the SphereBuilder
  const sphere = BABYLON.MeshBuilder.CreateSphere('sphere1', { segments: 16, diameter: 2, sideOrientation: BABYLON.Mesh.FRONTSIDE }, scene);
  // Move the sphere upward 1/2 of its height
  sphere.position.y = 1;
  // Create a built-in "ground" shape;
  BABYLON.MeshBuilder.CreateGround("ground1", { width: 6, height: 6, subdivisions: 2, updatable: false }, scene);
  // Return the created scene
  return scene;
};

export async function initGameEngine(canvasRef: React.RefObject<HTMLCanvasElement>) {
  const engine = new BABYLON.Engine(canvasRef.current, true, { preserveDrawingBuffer: true, stencil: true });
  engine.resize();

  const scene = await createScene(engine, canvasRef);

  engine.runRenderLoop(function () {
    scene.render();
  });

  return engine;
}
